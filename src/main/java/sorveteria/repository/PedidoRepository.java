package sorveteria.repository;

import sorveteria.model.Pedido;
import sorveteria.factory.Produto;
import sorveteria.state.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementa Repository com Integer para o ID
public class PedidoRepository implements Repository<Pedido, Integer> {

    @Override
    public Pedido salvar(Pedido pedido) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null; // Para obter chaves geradas
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // Se o pedido não tem ID (é novo), insere e obtém o ID gerado
            // Assumimos que ID = 0 significa um novo pedido a ser inserido.
            if (pedido.getId() == 0) {
                // NÃO INCLUI 'id' na lista de colunas para o INSERT
                String sqlInsert = "INSERT INTO pedidos (estado_atual, valor_total, data_criacao) VALUES (?, ?, ?)";
                stmt = conn.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS); // Indica para retornar chaves geradas
                stmt.setString(1, pedido.getEstado().getDescricao());
                stmt.setDouble(2, pedido.getValorTotal());
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.executeUpdate();

                rs = stmt.getGeneratedKeys(); // Obtém o ResultSet com as chaves geradas
                if (rs.next()) {
                    int generatedId = rs.getInt(1); // Recupera o primeiro (e único) ID gerado
                    pedido.setId(generatedId); // Define o ID gerado no objeto Pedido
                    System.out.println("Novo pedido inserido com ID gerado: #" + generatedId);
                }
            } else { // Se o pedido já tem ID, é uma atualização
                String sqlUpdate = "UPDATE pedidos SET estado_atual = ?, valor_total = ? WHERE id = ?";
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setString(1, pedido.getEstado().getDescricao());
                stmt.setDouble(2, pedido.getValorTotal());
                stmt.setInt(3, pedido.getId());
                stmt.executeUpdate();
                System.out.println("Pedido #" + pedido.getId() + " atualizado.");
            }

            // Após o pedido principal ser salvo/atualizado e o ID estar no objeto 'pedido',
            // podemos processar os itens.

            // Limpar itens antigos para este pedido (evitar duplicatas ao atualizar)
            String sqlDeleteItens = "DELETE FROM pedido_itens WHERE pedido_id = ?";
            stmt = conn.prepareStatement(sqlDeleteItens);
            stmt.setInt(1, pedido.getId());
            stmt.executeUpdate();

            // Salvar os novos itens do pedido
            String sqlInsertItem = "INSERT INTO pedido_itens (pedido_id, nome_produto, preco_unitario) VALUES (?, ?, ?)";
            for (Produto item : pedido.getItens()) {
                stmt = conn.prepareStatement(sqlInsertItem);
                stmt.setInt(1, pedido.getId());
                stmt.setString(2, item.getNome());
                stmt.setDouble(3, item.getPreco());
                stmt.executeUpdate();
            }

            conn.commit(); // Confirma a transação
            System.out.println("Itens do pedido #" + pedido.getId() + " salvos/atualizados com sucesso.");
            return pedido;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar/atualizar pedido (com itens): " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz a transação em caso de erro
                    System.err.println("Transação desfeita.");
                } catch (SQLException rbEx) {
                    System.err.println("Erro ao desfazer transação: " + rbEx.getMessage());
                }
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    @Override
    public Optional<Pedido> buscarPorId(Integer id) { // ID é Integer
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Pedido pedido = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sqlPedido = "SELECT id, estado_atual, valor_total FROM pedidos WHERE id = ?";
            stmt = conn.prepareStatement(sqlPedido);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int pedidoId = rs.getInt("id");
                String estadoDescricao = rs.getString("estado_atual");
                double valorTotal = rs.getDouble("valor_total");

                EstadoPedido estadoCarregado;

                switch (estadoDescricao) {
                    case "Em preparo":
                        estadoCarregado = new EmPreparoState();
                        break;
                    case "Pronto para entrega":
                        estadoCarregado = new ProntoParaEntregaState();
                        break;
                    case "Entregue":
                        estadoCarregado = new EntregueState();
                        break;
                    case "Cancelado":
                        estadoCarregado = new CanceladoState();
                        break;
                    case "Pedido recebido":
                    default:
                        estadoCarregado = new RecebidoState();
                        break;
                }
                pedido = new Pedido(pedidoId, valorTotal, estadoCarregado);

                String sqlItens = "SELECT nome_produto, preco_unitario FROM pedido_itens WHERE pedido_id = ?";
                stmt = conn.prepareStatement(sqlItens);
                stmt.setInt(1, pedidoId);
                ResultSet rsItens = stmt.executeQuery();
                while (rsItens.next()) {
                    String nomeProduto = rsItens.getString("nome_produto");
                    double precoUnitario = rsItens.getDouble("preco_unitario");
                    Produto itemCarregado = new Produto(nomeProduto, precoUnitario) {};
                    pedido.adicionarItem(itemCarregado);
                }
                rsItens.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pedido por ID (com itens): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return Optional.ofNullable(pedido);
    }

    @Override
    public List<Pedido> buscarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sqlPedidos = "SELECT id, estado_atual, valor_total FROM pedidos";
            stmt = conn.prepareStatement(sqlPedidos);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int pedidoId = rs.getInt("id");
                String estadoDescricao = rs.getString("estado_atual");
                double valorTotal = rs.getDouble("valor_total");

                EstadoPedido estadoCarregado;

                switch (estadoDescricao) {
                    case "Em preparo":
                        estadoCarregado = new EmPreparoState();
                        break;
                    case "Pronto para entrega":
                        estadoCarregado = new ProntoParaEntregaState();
                        break;
                    case "Entregue":
                        estadoCarregado = new EntregueState();
                        break;
                    case "Cancelado":
                        estadoCarregado = new CanceladoState();
                        break;
                    case "Pedido recebido":
                    default:
                        estadoCarregado = new RecebidoState();
                        break;
                }
                Pedido pedido = new Pedido(pedidoId, valorTotal, estadoCarregado);

                String sqlItens = "SELECT nome_produto, preco_unitario FROM pedido_itens WHERE pedido_id = ?";
                PreparedStatement stmtItens = conn.prepareStatement(sqlItens);
                stmtItens.setInt(1, pedidoId);
                ResultSet rsItens = stmtItens.executeQuery();
                while (rsItens.next()) {
                    String nomeProduto = rsItens.getString("nome_produto");
                    double precoUnitario = rsItens.getDouble("preco_unitario");
                    Produto itemCarregado = new Produto(nomeProduto, precoUnitario) {};
                    pedido.adicionarItem(itemCarregado);
                }
                rsItens.close();
                stmtItens.close();

                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os pedidos (com itens): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return pedidos;
    }

    @Override
    public void deletar(Integer id) { // ID é Integer
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // Deletar itens do pedido primeiro (devido à chave estrangeira)
            String sqlDeleteItens = "DELETE FROM pedido_itens WHERE pedido_id = ?";
            stmt = conn.prepareStatement(sqlDeleteItens);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            // Deletar o pedido principal
            String sqlDeletePedido = "DELETE FROM pedidos WHERE id = ?";
            stmt = conn.prepareStatement(sqlDeletePedido);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            conn.commit(); // Confirma a transação
            System.out.println("Pedido com ID " + id + " (e seus itens) deletado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar pedido (e seus itens): " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz a transação
                    System.err.println("Transação desfeita.");
                } catch (SQLException rbEx) {
                    System.err.println("Erro ao desfazer transação: " + rbEx.getMessage());
                }
            }
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
    }
}