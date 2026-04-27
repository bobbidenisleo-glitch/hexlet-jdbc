package io.hexlet;

import java.sql.*;

/**
 * Демонстрация преимуществ try-with-resources перед ручным закрытием
 */
public class TryWithResourcesDemo {
    public static void main(String[] args) {
        System.out.println("=== Демонстрация try-with-resources ===\n");
        
        // ПЛОХОЙ ПРИМЕР: без try-with-resources
        System.out.println("1. ПЛОХОЙ ПРИМЕР (без try-with-resources):");
        badExample();
        
        // ХОРОШИЙ ПРИМЕР: с try-with-resources
        System.out.println("\n2. ХОРОШИЙ ПРИМЕР (с try-with-resources):");
        goodExample();
    }
    
    // Плохой пример: ручное закрытие с potential утечкой
    static void badExample() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection("jdbc:h2:mem:test_bad");
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE test (id INT)");
            rs = stmt.executeQuery("SELECT * FROM test");
            
            // Если здесь возникнет исключение, close() не вызовется!
            throw new SQLException("Внезапная ошибка!");
            
        } catch (SQLException e) {
            System.out.println("   Ошибка: " + e.getMessage());
        } finally {
            // Громоздкий и легко забываемый код закрытия
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.out.println("   Ошибка при закрытии rs: " + e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("   Ошибка при закрытии stmt: " + e.getMessage());
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("   Ошибка при закрытии conn: " + e.getMessage());
            }
        }
    }
    
    // Хороший пример: try-with-resources автоматически закрывает всё
    static void goodExample() {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_good");
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE test (id INT)");
            
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM test")) {
                // Работа с ResultSet
                System.out.println("   Запрос выполнен успешно");
            } // ResultSet автоматически закрывается
            
            System.out.println("   Все ресурсы будут закрыты автоматически");
            
        } catch (SQLException e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }
        // Connection и Statement автоматически закрываются здесь
    }
}
