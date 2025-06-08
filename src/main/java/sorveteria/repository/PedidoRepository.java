package sorveteria.repository;

import sorveteria.model.Pedido;
import sorveteria.factory.Produto; // Importar Produto
import sorveteria.factory.Factory; // Importar Factory para recriar produtos
import sorveteria.decorator.SaborBase; // Importar SaborBase para recriar produtos

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoRepository implements Repository<Pedido, String> {

    private Factory produtoFactory = new Factory();

    @Override
    public Pedido salvar(Pedido pedido) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Salvar/Atualizar o Pedido principal
            String sqlPedido;
            if (buscarPorId(pedido.getId()).isEmpty()) { // Se o pedido não existe, insere
                sqlPedido = "INSERT INTO pedidos (id, estado_atual, valor_total, data_criacao) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sqlPedido);
                stmt.setString(1, pedido.getId());
                stmt.setString(2, pedido.getEstado().getDescricao());
                stmt.setDouble(3, pedido.getValorTotal()); // Salva o valor total
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            } else { // Se o pedido existe, atualiza o estado e valor total
                sqlPedido = "UPDATE pedidos SET estado_atual = ?, valor_total = ? WHERE id = ?";
                stmt = conn.prepareStatement(sqlPedido);
                stmt.setString(1, pedido.getEstado().getDescricao());
                stmt.setDouble(2, pedido.getValorTotal()); // Atualiza o valor total
                stmt.setString(3, pedido.getId());
            }
            stmt.executeUpdate();

            // 2. Limpar itens antigos para este pedido (evitar duplicatas ao atualizar)
            String sqlDeleteItens = "DELETE FROM pedido_itens WHERE pedido_id = ?";
            stmt = conn.prepareStatement(sqlDeleteItens);
            stmt.setString(1, pedido.getId());
            stmt.executeUpdate();

            // 3. Salvar os novos itens do pedido
            String sqlInsertItem = "INSERT INTO pedido_itens (pedido_id, nome_produto, preco_unitario) VALUES (?, ?, ?)";
            for (Produto item : pedido.getItens()) {
                stmt = conn.prepareStatement(sqlInsertItem);
                stmt.setString(1, pedido.getId());
                stmt.setString(2, item.getNome());
                stmt.setDouble(3, item.getPreco());
                stmt.executeUpdate();
            }

            conn.commit(); // Confirma a transação
            System.out.println("Pedido salvo/atualizado (com itens) com sucesso: #" + pedido.getId());
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
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Pedido pedido = null;

        try {
            conn = DatabaseConnection.getConnection();

            // 1. Buscar os dados principais do Pedido
            String sqlPedido = "SELECT id, estado_atual, valor_total FROM pedidos WHERE id = ?";
            stmt = conn.prepareStatement(sqlPedido);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String pedidoId = rs.getString("id");
                String estadoDescricao = rs.getString("estado_atual");
                double valorTotal = rs.getDouble("valor_total"); // Carrega o valor total

                pedido = new Pedido(pedidoId, valorTotal); // Usa o novo construtor

                // Definir o estado (lógica de recriação do objeto State)
                switch (estadoDescricao) {
                    case "Em preparo":
                        pedido.setEstado(new sorveteria.state.EmPreparoState());
                        break;
                    case "Pronto para entrega":
                        pedido.setEstado(new sorveteria.state.ProntoParaEntregaState());
                        break;
                    case "Entregue":
                        pedido.setEstado(new sorveteria.state.EntregueState());
                        break;
                    case "Cancelado":
                        pedido.setEstado(new sorveteria.state.CanceladoState());
                        break;
                    case "Pedido recebido":
                    default:
                        pedido.setEstado(new sorveteria.state.RecebidoState());
                        break;
                }

                // 2. Buscar os itens associados a este Pedido
                String sqlItens = "SELECT nome_produto, preco_unitario FROM pedido_itens WHERE pedido_id = ?";
                stmt = conn.prepareStatement(sqlItens);
                stmt.setString(1, pedidoId);
                ResultSet rsItens = stmt.executeQuery();
                while (rsItens.next()) {
                    String nomeProduto = rsItens.getString("nome_produto");
                    double precoUnitario = rsItens.getDouble("preco_unitario");
                    Produto itemCarregado = new Produto(nomeProduto, precoUnitario) {}; // Classe anônima para Produto abstrata
                    pedido.adicionarItem(itemCarregado); // Adiciona o item ao pedido
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
                String pedidoId = rs.getString("id");
                String estadoDescricao = rs.getString("estado_atual");
                double valorTotal = rs.getDouble("valor_total");

                Pedido pedido = new Pedido(pedidoId, valorTotal);

                // Definir o estado
                switch (estadoDescricao) {
                    case "Em preparo":
                        pedido.setEstado(new sorveteria.state.EmPreparoState());
                        break;
                    case "Pronto para entrega":
                        pedido.setEstado(new sorveteria.state.ProntoParaEntregaState());
                        break;
                    case "Entregue":
                        pedido.setEstado(new sorveteria.state.EntregueState());
                        break;
                    case "Cancelado":
                        pedido.setEstado(new sorveteria.state.CanceladoState());
                        break;
                    case "Pedido recebido":
                    default:
                        pedido.setEstado(new sorveteria.state.RecebidoState());
                        break;
                }

                // Buscar itens para cada pedido
                String sqlItens = "SELECT nome_produto, preco_unitario FROM pedido_itens WHERE pedido_id = ?";
                PreparedStatement stmtItens = conn.prepareStatement(sqlItens);
                stmtItens.setString(1, pedidoId);
                ResultSet rsItens = stmtItens.executeQuery();
                while (rsItens.next()) {
                    String nomeProduto = rsItens.getString("nome_produto");
                    double precoUnitario = rsItens.getDouble("preco_unitario");
                    Produto itemCarregado = new Produto(nomeProduto, precoUnitario) {}; // Classe anônima
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
    public void deletar(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Deletar itens do pedido primeiro (devido à chave estrangeira)
            String sqlDeleteItens = "DELETE FROM pedido_itens WHERE pedido_id = ?";
            stmt = conn.prepareStatement(sqlDeleteItens);
            stmt.setString(1, id);
            stmt.executeUpdate();

            // 2. Deletar o pedido principal
            String sqlDeletePedido = "DELETE FROM pedidos WHERE id = ?";
            stmt = conn.prepareStatement(sqlDeletePedido);
            stmt.setString(1, id);
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