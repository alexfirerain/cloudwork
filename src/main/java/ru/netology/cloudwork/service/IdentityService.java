package ru.netology.cloudwork.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.model.UserInfo;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class IdentityService {

    private final String signingKey = "xxx";

    public String generateTokenFor(Authentication authentication) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();

//        return "right_token";
    }
}
