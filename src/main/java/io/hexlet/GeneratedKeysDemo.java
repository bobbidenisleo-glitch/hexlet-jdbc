package io.hexlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneratedKeysDemo {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:products_db";
        
        System.out.println("=== Демонстрация получения сгенерированных ключей ===\n");
        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            // Создаем таблицу продуктов
            createProductsTable(conn);
            
            // Добавляем продукт и получаем его ID
            long productId = addProductAndGetId(conn, "iPhone 15", 999.99);
            System.out.printf("✓ Продукт добавлен с ID: %d%n", productId);
            
            // Добавляем несколько продуктов и получаем их ID
            System.out.println("\n--- Добавление нескольких продуктов ---");
            List<Long> productIds = addMultipleProductsAndGetIds(conn);
            System.out.println("Полученные ID: " + productIds);
            
            // Используем полученные ID для связанных операций
            System.out.println("\n--- Использование полученных ID ---");
            for (Long id : productIds) {
                addProductReview(conn, id, "Отличный продукт!", 5);
                System.out.printf("✓ Добавлен отзыв для продукта ID: %d%n", id);
            }
            
            // Выводим все продукты с отзывами
            System.out.println("\n--- Все продукты с отзывами ---");
            displayProductsWithReviews(conn);
            
        } catch (SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createProductsTable(Connection conn) throws SQLException {
        String createProductsSQL = """
            CREATE TABLE IF NOT EXISTS products (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL,
                price DECIMAL(10, 2)
            )
            """;
        
        String createReviewsSQL = """
            CREATE TABLE IF NOT EXISTS reviews (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                product_id BIGINT,
                comment VARCHAR(500),
                rating INT,
                FOREIGN KEY (product_id) REFERENCES products(id)
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProductsSQL);
            stmt.execute(createReviewsSQL);
            System.out.println("✓ Таблицы созданы");
        }
    }
    
    // Добавление одного продукта и получение его ID
    private static long addProductAndGetId(Connection conn, String name, double price) throws SQLException {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ID");
                }
            }
        }
    }
    
    // Добавление нескольких продуктов и получение их ID
    private static List<Long> addMultipleProductsAndGetIds(Connection conn) throws SQLException {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
        List<Long> ids = new ArrayList<>();
        
        Object[][] products = {
            {"Samsung Galaxy", 899.99},
            {"Google Pixel", 799.99},
            {"Xiaomi Mi", 599.99}
        };
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Object[] product : products) {
                pstmt.setString(1, (String) product[0]);
                pstmt.setDouble(2, (Double) product[1]);
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ids.add(generatedKeys.getLong(1));
                        System.out.printf("✓ Добавлен продукт: %s, ID: %d%n", product[0], generatedKeys.getLong(1));
                    }
                }
            }
        }
        
        return ids;
    }
    
    // Добавление отзыва к продукту
    private static void addProductReview(Connection conn, long productId, String comment, int rating) throws SQLException {
        String sql = "INSERT INTO reviews (product_id, comment, rating) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, productId);
            pstmt.setString(2, comment);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();
        }
    }
    
    // Вывод продуктов с отзывами
    private static void displayProductsWithReviews(Connection conn) throws SQLException {
        String sql = """
            SELECT p.id, p.name, p.price, 
                   COUNT(r.id) as review_count,
                   AVG(r.rating) as avg_rating
            FROM products p
            LEFT JOIN reviews r ON p.id = r.product_id
            GROUP BY p.id, p.name, p.price
            ORDER BY p.id
            """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.printf("ID: %d | %s | $%.2f | Отзывов: %d | Рейтинг: %.1f%n",
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getLong("review_count"),
                    rs.getDouble("avg_rating"));
            }
        }
    }
}
