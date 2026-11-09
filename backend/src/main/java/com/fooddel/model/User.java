package com.fooddel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Same "users" collection as before. Passwords are bcrypt hashes, so existing accounts can still log in.
 * cartData maps foodId -> quantity, e.g. {"66acb38e1fa8a9d4a9c08a78": 2}.
 * Never returned to the client.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private Map<String, Object> cartData = new HashMap<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Map<String, Object> getCartData() { return cartData; }
    public void setCartData(Map<String, Object> cartData) { this.cartData = cartData; }
}
