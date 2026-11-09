package com.fooddel;

import com.fooddel.security.JwtService;
import com.fooddel.util.Api;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtAndPasswordTest {

    @Test
    void jwtRoundTripWithShortSecret() {
        // The real secret is 11 chars. jjwt would refuse it; java-jwt must accept it.
        JwtService jwt = new JwtService("short-key");
        assertEquals("66acb38e1fa8a9d4a9c08a78", jwt.verify(jwt.createToken("66acb38e1fa8a9d4a9c08a78")));
    }

    @Test
    void jwtRejectsWrongSecretAndGarbage() {
        String token = new JwtService("short-key").createToken("abc");
        assertNull(new JwtService("other-key").verify(token));
        assertNull(new JwtService("short-key").verify("not.a.token"));
    }

    @Test
    void acceptsTokenBuiltTheWayNodeJsonwebtokenBuildsIt() throws Exception {
        // jsonwebtoken.sign({id}, secret): header {"alg":"HS256","typ":"JWT"}, payload {"id","iat"}, HMAC-SHA256.
        String token = nodeSign("{\"id\":\"66acb38e1fa8a9d4a9c08a78\",\"iat\":1722594190}", "short-key");
        assertEquals("66acb38e1fa8a9d4a9c08a78", new JwtService("short-key").verify(token));
    }

    @Test
    void bcryptAccepts2bHashesLikeNodeBcrypt() {
        BCryptPasswordEncoder node = new BCryptPasswordEncoder(BCryptVersion.$2B, 10);
        String hash = node.encode("password123");
        assertTrue(hash.startsWith("$2b$10$"));

        BCryptPasswordEncoder app = new BCryptPasswordEncoder(10);
        assertTrue(app.matches("password123", hash));
        assertFalse(app.matches("wrong-pass", hash));
    }

    @Test
    void compactWritesWholeNumbersWithoutDecimal() {
        assertEquals(18L, Api.compact(18.0));
        assertEquals(12.5, Api.compact(12.5));
        assertNull(Api.compact(null));
    }

    private static String nodeSign(String payloadJson, String secret) throws Exception {
        Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
        String header = b64.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = b64.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal((header + "." + payload).getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + b64.encodeToString(sig);
    }
}
