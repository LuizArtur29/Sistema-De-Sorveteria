package sorveteria.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://ep-frosty-mouse-acb0hod3-pooler.sa-east-1.aws.neon.tech/sorveteria-project?user=sorveteria-project_owner&password=npg_E7DnVKOa3XSl&sslmode=require";
    private static final String USER = "sorveteria-project_owner";
    private static final String PASS = "npg_E7DnVKOa3XSl";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
            }
        }
    }
}
