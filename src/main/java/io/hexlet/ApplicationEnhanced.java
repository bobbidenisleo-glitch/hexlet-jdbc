package io.hexlet;

import java.sql.*;

public class ApplicationEnhanced {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:hexlet_test";
        
        System.out.println("=== Улучшенная версия с try-with-resources ===\n");
        
        // Connection автоматически закроется
        try (Connection conn = DriverManager.getConnection(url)) {
            
            // Создание таблицы - Statement закроется автоматически
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(255) NOT NULL,
                    phone VARCHAR(255),
                    email VARCHAR(255)
                )
                """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("✓ Таблица создана/проверена");
            }
            
            // Вставка нескольких пользователей
            String[][] users = {
                {"tommy", "123456789", "tommy@example.com"},
                {"alice", "987654321", "alice@example.com"},
                {"bob", "555123456", "bob@example.com"}
            };
            
            for (String[] user : users) {
                String insertSQL = String.format(
                    "INSERT INTO users (username, phone, email) VALUES ('%s', '%s', '%s')",
                    user[0], user[1], user[2]
                );
                try (Statement stmt = conn.createStatement()) {
                    int rows = stmt.executeUpdate(insertSQL);
                    System.out.printf("✓ Добавлен пользователь: %s (затронуто строк: %d)%n", user[0], rows);
                }
            }
            
            // Выборка всех данных - закрываются и Statement, и ResultSet
            System.out.println("\n--- Список всех пользователей ---");
            String selectSQL = "SELECT * FROM users ORDER BY id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String username = rs.getString("username");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    
                    System.out.printf("ID: %d | %-10s | %-15s | %s%n", 
                        id, username, phone, email);
                }
            }
            
            // Обновление данных
            System.out.println("\n--- Обновление телефона tommy ---");
            String updateSQL = "UPDATE users SET phone = '111222333' WHERE username = 'tommy'";
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(updateSQL);
                System.out.printf("✓ Обновлено строк: %d%n", updated);
            }
            
            // Проверка после обновления
            System.out.println("\n--- Данные после обновления ---");
            String checkSQL = "SELECT username, phone FROM users WHERE username = 'tommy'";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSQL)) {
                if (rs.next()) {
                    System.out.printf("Пользователь: %s, Новый телефон: %s%n", 
                        rs.getString("username"), rs.getString("phone"));
                }
            }
            
            // Удаление пользователя
            System.out.println("\n--- Удаление пользователя bob ---");
            String deleteSQL = "DELETE FROM users WHERE username = 'bob'";
            try (Statement stmt = conn.createStatement()) {
                int deleted = stmt.executeUpdate(deleteSQL);
                System.out.printf("✓ Удалено строк: %d%n", deleted);
            }
            
            // Финальная выборка
            System.out.println("\n--- Финальный список пользователей ---");
            String finalSQL = "SELECT * FROM users ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(finalSQL)) {
                
                while (rs.next()) {
                    System.out.printf("ID: %d | %s | %s%n",
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("phone"));
                }
            }
            
            // Получение метаданных
            System.out.println("\n--- Метаданные базы данных ---");
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("JDBC Driver: " + metaData.getDriverName());
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            
            System.out.println("\n✓ Все ресурсы автоматически закрыты!");
            
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с БД:");
            System.err.println("Сообщение: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
    }
}
