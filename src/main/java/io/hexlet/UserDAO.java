package io.hexlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private Connection connection;
    
    public UserDAO(Connection conn) {
        this.connection = conn;
    }
    
    // Сохранение пользователя (создание или обновление)
    public void save(User user) throws SQLException {
        if (user.getId() == null) {
            // Новый пользователь - выполняем INSERT
            String sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPhone());
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Не удалось получить ID после сохранения пользователя");
                    }
                }
            }
        } else {
            // Существующий пользователь - выполняем UPDATE
            String sql = "UPDATE users SET username = ?, phone = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPhone());
                pstmt.setLong(3, user.getId());
                pstmt.executeUpdate();
            }
        }
    }
    
    // Поиск пользователя по ID
    public Optional<User> find(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String phone = rs.getString("phone");
                    User user = new User(username, phone);
                    user.setId(id);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }
    
    // Поиск всех пользователей
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                User user = new User(id, username, phone);
                users.add(user);
            }
        }
        return users;
    }
    
    // Поиск пользователя по имени
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String phone = rs.getString("phone");
                    User user = new User(id, username, phone);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        }
    }
    
    // Удаление пользователя по ID
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Удаление пользователя по объекту
    public boolean delete(User user) throws SQLException {
        if (user.getId() == null) {
            return false;
        }
        return delete(user.getId());
    }
    
    // Обновление телефона пользователя
    public boolean updatePhone(Long userId, String newPhone) throws SQLException {
        String sql = "UPDATE users SET phone = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPhone);
            pstmt.setLong(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Проверка существования пользователя
    public boolean exists(Long id) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
