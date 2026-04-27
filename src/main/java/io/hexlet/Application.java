package io.hexlet;

import java.sql.*;

public class Application {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:h2:mem:hexlet_test";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            // Создаем таблицу
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    phone VARCHAR(255)
                )
                """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Таблица users создана");
            }
            
            // Добавляем несколько пользователей, используя один PreparedStatement
            String insertSQL = "INSERT INTO users (username, phone) VALUES (?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                // Добавляем первого пользователя
                pstmt.setString(1, "Tommy");
                pstmt.setString(2, "123456789");
                pstmt.executeUpdate();
                
                // Добавляем второго пользователя
                pstmt.setString(1, "Maria");
                pstmt.setString(2, "987654321");
                pstmt.executeUpdate();
                
                // Добавляем третьего пользователя
                pstmt.setString(1, "Sarah");
                pstmt.setString(2, "555555555");
                pstmt.executeUpdate();
                
                System.out.println("Добавлено 3 пользователя с помощью одного PreparedStatement");
            }
            
            // Выводим всех пользователей
            System.out.println("\nСписок всех пользователей:");
            String selectSQL = "SELECT * FROM users ORDER BY id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                
                while (rs.next()) {
                    System.out.printf("ID: %d, Username: %s, Phone: %s%n",
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("phone"));
                }
            }
            
            // Удаляем пользователя по имени с использованием PreparedStatement
            String deleteSQL = "DELETE FROM users WHERE username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                pstmt.setString(1, "Tommy");
                int rowsDeleted = pstmt.executeUpdate();
                System.out.printf("\nУдален пользователь Tommy (удалено строк: %d)%n", rowsDeleted);
            }
            
            // Выводим пользователей после удаления
            System.out.println("\nСписок пользователей после удаления Tommy:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                
                while (rs.next()) {
                    System.out.printf("ID: %d, Username: %s, Phone: %s%n",
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("phone"));
                }
            }
        }
    }
}
