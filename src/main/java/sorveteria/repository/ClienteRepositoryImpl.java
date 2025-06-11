package sorveteria.repository;

import sorveteria.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (id, nome, email) VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET nome = EXCLUDED.nome, email = EXCLUDED.email";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cliente.getId());
            stmt.setString(2, cliente.getNome());
            stmt.setString(3, cliente.getEmail());
            stmt.executeUpdate();
            System.out.println("Cliente " + cliente.getNome() + " salvo/atualizado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        String sql = "SELECT id, nome, email FROM clientes WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Cliente(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nome, email FROM clientes";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os clientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
        return clientes;
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, email = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente com ID " + cliente.getId() + " atualizado com sucesso.");
            } else {
                System.out.println("Cliente com ID " + cliente.getId() + " não encontrado para atualização.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public void deletar(String id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente com ID " + id + " deletado com sucesso.");
            } else {
                System.out.println("Cliente com ID " + id + " não encontrado para deleção.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            DatabaseConnection.closeConnection(conn);
        }
    }
}