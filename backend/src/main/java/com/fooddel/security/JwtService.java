package com.fooddel.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Issues and checks the login token.
 * Payload is {"id": "<userId>", "iat": ...}, signed HS256 with JWT_SECRET.
 * Same format as the common Node "jsonwebtoken" library, so tokens work across both.
 */
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(@Value("${app.jwt-secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not set");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }

    public String createToken(String userId) {
        return JWT.create()
                .withClaim("id", userId)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /** Returns the user id, or null when the token is bad or tampered. */
    public String verify(String token) {
        try {
            return verifier.verify(token).getClaim("id").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
