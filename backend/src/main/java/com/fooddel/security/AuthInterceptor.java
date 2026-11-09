package com.fooddel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddel.util.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * Guards the logged-in routes (cart, place order, my orders).
 * Reads the "token" header (not "Authorization"), because that is what the React app sends.
 * On success it puts the user id in a request attribute for the controller.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID = "userId";

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // CORS preflight carries no token
        }
        String token = request.getHeader("token");
        if (token == null || token.isBlank()) {
            return reject(response, Api.fail("Not Authorized Login Again"));
        }
        String userId = jwtService.verify(token);
        if (userId == null) {
            return reject(response, Api.fail("Error"));
        }
        request.setAttribute(USER_ID, userId);
        return true;
    }

    // Old API answered auth failures with HTTP 200 + success:false. Kept so the UI behaves the same.
    private boolean reject(HttpServletResponse response, Map<String, Object> body) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
        return false;
    }
}
