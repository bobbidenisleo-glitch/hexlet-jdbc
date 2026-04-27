package io.hexlet;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private Connection conn;
    private UserDAO userDAO;
    
    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:test_db");
        userDAO = new UserDAO(conn);
        
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
        }
    }
    
    @AfterEach
    void tearDown() throws SQLException {
        conn.close();
    }
    
    @Test
    void testSaveAndFind() throws SQLException {
        User user = new User("John", "123456");
        userDAO.save(user);
        
        assertNotNull(user.getId());
        
        User found = userDAO.find(user.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(user.getPhone(), found.getPhone());
    }
    
    @Test
    void testDelete() throws SQLException {
        User user = new User("Jane", "654321");
        userDAO.save(user);
        
        assertTrue(userDAO.delete(user.getId()));
        assertFalse(userDAO.find(user.getId()).isPresent());
    }
    
    @Test
    void testUpdate() throws SQLException {
        User user = new User("Bob", "111111");
        userDAO.save(user);
        
        user.setPhone("999999");
        userDAO.save(user);
        
        User updated = userDAO.find(user.getId()).orElse(null);
        assertEquals("999999", updated.getPhone());
    }
}
