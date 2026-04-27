package io.hexlet;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Application {
    public static void main(String[] args) throws SQLException {
        System.out.println("Подключение к базе данных...");
        var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test");
        
        try {
            // Создаем таблицу
            System.out.println("Создание таблицы users...");
            var createSql = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            var statement = conn.createStatement();
            statement.execute(createSql);
            statement.close();
            System.out.println("Таблица создана");
            
            // Добавляем данные
            System.out.println("Добавление пользователя tommy...");
            var insertSql = "INSERT INTO users (username, phone) VALUES ('tommy', '123456789')";
            var statement2 = conn.createStatement();
            var affectedRows = statement2.executeUpdate(insertSql);
            statement2.close();
            System.out.println("Добавлено строк: " + affectedRows);
            
            // Добавляем второго пользователя
            System.out.println("Добавление пользователя alice...");
            var insertSql2 = "INSERT INTO users (username, phone) VALUES ('alice', '987654321')";
            var statement3 = conn.createStatement();
            statement3.executeUpdate(insertSql2);
            statement3.close();
            System.out.println("Пользователь alice добавлен");
            
            // Выбираем данные
            System.out.println("\nВыборка данных из таблицы users:");
            var selectSql = "SELECT * FROM users";
            var statement4 = conn.createStatement();
            var resultSet = statement4.executeQuery(selectSql);
            
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                System.out.println("ID: " + id + ", Username: " + username + ", Phone: " + phone);
            }
            
            resultSet.close();
            statement4.close();
            
        } finally {
            conn.close();
            System.out.println("\nСоединение закрыто");
        }
    }
}
