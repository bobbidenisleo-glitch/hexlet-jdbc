package io.hexlet;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Application {
    public static void main(String[] args) throws SQLException {
        // Соединение автоматически закроется после блока try
        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {
            
            System.out.println("=== JDBC Demo с H2 Database (try-with-resources) ===\n");
            
            // 1. Создаем таблицу
            System.out.println("1. Создание таблицы users...");
            String createTableSQL = """
                CREATE TABLE users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(255),
                    phone VARCHAR(255)
                )
                """;
            
            try (var statement = conn.createStatement()) {
                statement.execute(createTableSQL);
                System.out.println("   ✓ Таблица создана\n");
            }
            
            // 2. Добавляем первого пользователя
            System.out.println("2. Добавление пользователя tommy...");
            String insertSQL1 = "INSERT INTO users (username, phone) VALUES ('tommy', '123456789')";
            try (var statement = conn.createStatement()) {
                int rowsInserted = statement.executeUpdate(insertSQL1);
                System.out.println("   ✓ Добавлено строк: " + rowsInserted + "\n");
            }
            
            // 3. Добавляем второго пользователя
            System.out.println("3. Добавление пользователя alice...");
            String insertSQL2 = "INSERT INTO users (username, phone) VALUES ('alice', '987654321')";
            try (var statement = conn.createStatement()) {
                statement.executeUpdate(insertSQL2);
                System.out.println("   ✓ Пользователь alice добавлен\n");
            }
            
            // 4. Выбираем и выводим всех пользователей
            System.out.println("4. Список всех пользователей:");
            String selectSQL = "SELECT * FROM users";
            
            try (var statement = conn.createStatement();
                 var resultSet = statement.executeQuery(selectSQL)) {
                
                System.out.println("   " + "-".repeat(40));
                while (resultSet.next()) {
                    var id = resultSet.getLong("id");
                    var username = resultSet.getString("username");
                    var phone = resultSet.getString("phone");
                    System.out.printf("   | ID: %d | Username: %-10s | Phone: %s |\n", id, username, phone);
                }
                System.out.println("   " + "-".repeat(40));
            }
            
            System.out.println("\n✓ Все ресурсы автоматически закрыты (Connection, Statement, ResultSet)");
            
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
            throw e;
        }
    }
}
