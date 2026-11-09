package com.fooddel.controller;

import com.fooddel.model.User;
import com.fooddel.repository.UserRepository;
import com.fooddel.security.JwtService;
import com.fooddel.util.Api;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // Simple shape check: something@something.tld, no spaces.
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwt;

    public UserController(UserRepository users, BCryptPasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /** Body: {"email": "...", "password": "..."}. Returns {"success": true, "token": "..."}. */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody(required = false) Map<String, Object> body) {
        String email = str(body, "email");
        String password = str(body, "password");
        if (email == null || password == null) {
            return Api.fail("User doesnot exists");
        }
        User user = users.findByEmail(email).orElse(null);
        if (user == null) {
            return Api.fail("User doesnot exists");
        }
        if (!encoder.matches(password, user.getPassword())) {
            return Api.fail("Invalid Credentials");
        }
        return Api.ok("token", jwt.createToken(user.getId()));
    }

    /** Body: {"name": "...", "email": "...", "password": "..."}. */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody(required = false) Map<String, Object> body) {
        String name = str(body, "name");
        String email = str(body, "email");
        String password = str(body, "password");

        if (email != null && users.existsByEmail(email)) {
            return Api.fail("User already exists");
        }
        if (email == null || !EMAIL.matcher(email).matches()) {
            return Api.fail("Please enter a valid email");
        }
        if (password == null || password.length() < 8) {
            return Api.fail("Enter enter a strong min 8 char password");
        }
        if (name == null || name.isBlank()) {
            return Api.fail("Error");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        try {
            user = users.save(user);
        } catch (DuplicateKeyException e) {
            // Two sign-ups with the same email at the same time: the unique index on email catches it.
            return Api.fail("User already exists");
        }
        return Api.ok("token", jwt.createToken(user.getId()));
    }

    private static String str(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }
}
