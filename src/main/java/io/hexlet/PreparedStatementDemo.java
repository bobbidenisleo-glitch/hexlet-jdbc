package io.hexlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreparedStatementDemo {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:users_db";
        
        System.out.println("=== Демонстрация PreparedStatement ===\n");
        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            // Создаем таблицу пользователей
            createTable(conn);
            
            // 1. Добавляем нескольких пользователей, используя один PreparedStatement
            addMultipleUsers(conn);
            
            // 2. Выводим всех пользователей
            System.out.println("\n--- Все пользователи после добавления ---");
            getAllUsers(conn);
            
            // 3. Удаляем пользователя по имени
            deleteUserByName(conn, "Tommy");
            
            // 4. Выводим пользователей после удаления
            System.out.println("\n--- Все пользователи после удаления Tommy ---");
            getAllUsers(conn);
            
            // 5. Дополнительно: демонстрация защиты от SQL-инъекций
            demonstrateSQLInjectionProtection(conn);
            
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTable(Connection conn) throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL UNIQUE,
                phone VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("✓ Таблица users создана/проверена");
        }
    }
    
    // Добавление нескольких пользователей с использованием одного PreparedStatement
    private static void addMultipleUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
        
        // Массив пользователей для добавления
        String[][] users = {
            {"Tommy", "123456789"},
            {"Maria", "987654321"},
            {"Sarah", "555555555"},
            {"John", "444444444"},
            {"Alice", "333333333"}
        };
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] user : users) {
                pstmt.setString(1, user[0]);  // username
                pstmt.setString(2, user[1]);  // phone
                int rowsAffected = pstmt.executeUpdate();
                System.out.printf("✓ Добавлен пользователь: %s (затронуто строк: %d)%n", user[0], rowsAffected);
            }
        }
        
        System.out.println("\n✓ Все пользователи добавлены с использованием одного PreparedStatement");
    }
    
    // Получение всех пользователей
    private static void getAllUsers(Connection conn) throws SQLException {
        String sql = "SELECT id, username, phone, created_at FROM users ORDER BY id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("ID | Username | Phone | Created At");
            System.out.println("---|----------|-------|------------");
            while (rs.next()) {
                System.out.printf("%d | %-8s | %-9s | %s%n",
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("phone"),
                    rs.getTimestamp("created_at"));
            }
        }
    }
    
    // Удаление пользователя по имени с использованием PreparedStatement
    private static void deleteUserByName(Connection conn, String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int rowsDeleted = pstmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.printf("\n✓ Удален пользователь: %s (удалено строк: %d)%n", username, rowsDeleted);
            } else {
                System.out.printf("\n⚠ Пользователь %s не найден%n", username);
            }
        }
    }
    
    // Демонстрация защиты от SQL-инъекций
    private static void demonstrateSQLInjectionProtection(Connection conn) throws SQLException {
        System.out.println("\n=== Демонстрация защиты от SQL-инъекций ===");
        
        // Зловредные данные, которые пытаются взломать систему
        String maliciousInput = "'; DELETE FROM users; --";
        
        System.out.println("\nПлохой пример (конкатенация строк):");
        System.out.println("Пользовательский ввод: " + maliciousInput);
        
        // ОПАСНО! Никогда так не делайте!
        String unsafeSQL = "SELECT * FROM users WHERE username = '" + maliciousInput + "'";
        System.out.println("Сформированный SQL: " + unsafeSQL);
        System.out.println("⚠ Этот запрос мог бы удалить всех пользователей!");
        
        System.out.println("\nХороший пример (PreparedStatement):");
        String safeSQL = "SELECT * FROM users WHERE username = ?";
        System.out.println("Шаблон SQL: " + safeSQL);
        
        try (PreparedStatement pstmt = conn.prepareStatement(safeSQL)) {
            pstmt.setString(1, maliciousInput);
            System.out.println("PreparedStatement автоматически экранирует опасные символы");
            System.out.println("Зловредный ввод обрабатывается как обычная строка, а не как SQL код");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("✓ Запрос выполнен безопасно, данные не повреждены");
            }
        }
    }
}
