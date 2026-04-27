package io.hexlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public static void main(String[] args) throws SQLException {
        System.out.println("=== DAO Demo ===\n");
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {
            
            // Создаем таблицу
            createTable(conn);
            
            // Создаем DAO
            UserDAO userDAO = new UserDAO(conn);
            
            // 1. Создаем нового пользователя
            System.out.println("1. Создание нового пользователя:");
            User user1 = new User("Tommy", "123456789");
            System.out.println("   До сохранения: " + user1);
            userDAO.save(user1);
            System.out.println("   После сохранения: " + user1);
            
            // 2. Создаем еще одного пользователя
            User user2 = new User("Alice", "987654321");
            userDAO.save(user2);
            System.out.println("   Создан: " + user2);
            
            // 3. Поиск пользователя по ID
            System.out.println("\n2. Поиск пользователя по ID:");
            userDAO.find(user1.getId()).ifPresentOrElse(
                u -> System.out.println("   Найден: " + u),
                () -> System.out.println("   Пользователь не найден")
            );
            
            // 4. Обновление пользователя
            System.out.println("\n3. Обновление пользователя:");
            user1.setPhone("111222333");
            userDAO.save(user1);
            System.out.println("   Обновлен: " + user1);
            
            // 5. Поиск по имени
            System.out.println("\n4. Поиск по имени:");
            userDAO.findByUsername("Alice").ifPresentOrElse(
                u -> System.out.println("   Найден: " + u),
                () -> System.out.println("   Пользователь не найден")
            );
            
            // 6. Все пользователи
            System.out.println("\n5. Все пользователи:");
            userDAO.findAll().forEach(u -> System.out.println("   " + u));
            
            // 7. Удаление пользователя
            System.out.println("\n6. Удаление пользователя:");
            System.out.println("   Удален Tommy: " + userDAO.delete(user1));
            
            // 8. Пользователи после удаления
            System.out.println("\n7. Пользователи после удаления:");
            userDAO.findAll().forEach(u -> System.out.println("   " + u));
            
            // 9. Проверка существования
            System.out.println("\n8. Проверка существования:");
            System.out.println("   Существует ли Tommy? " + userDAO.exists(user1.getId()));
            System.out.println("   Существует ли Alice? " + userDAO.exists(user2.getId()));
            
            // 10. Обновление телефона
            System.out.println("\n9. Обновление телефона:");
            userDAO.updatePhone(user2.getId(), "555555555");
            userDAO.find(user2.getId()).ifPresent(u -> 
                System.out.println("   Обновлен телефон: " + u)
            );
        }
    }
    
    private static void createTable(Connection conn) throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL UNIQUE,
                phone VARCHAR(255)
            )
            """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("✓ Таблица users создана\n");
        }
    }
}
