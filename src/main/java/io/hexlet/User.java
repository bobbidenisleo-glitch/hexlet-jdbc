package io.hexlet;

import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String phone;
    
    public User(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }
    
    public User(Long id, String username, String phone) {
        this.id = id;
        this.username = username;
        this.phone = phone;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', phone='%s'}", id, username, phone);
    }
}
