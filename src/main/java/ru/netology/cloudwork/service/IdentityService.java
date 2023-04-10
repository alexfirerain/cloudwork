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

    private final String signingKey = "ymLTU8Pq8aj4fmJZj60w24OrMNu1tIj4TVJ";

    public String generateTokenFor(Authentication authentication) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        Instant moment = Instant.now();
        SecretKey cipher = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(moment))
                .setExpiration(Date.from(moment.plus(1, ChronoUnit.HOURS)))
                .signWith(cipher)
                .compact();

//        return "right_token";
    }
}
