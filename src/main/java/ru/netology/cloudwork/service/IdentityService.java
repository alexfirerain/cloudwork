package ru.netology.cloudwork.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.netology.cloudwork.model.UserInfo;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class IdentityService implements AuthenticationManager {

//    private final String signingKey = "ymLTU8Pq8aj4fmJZj60w24OrMNu1tIj4TVJ";

    public String generateTokenFor(Authentication authentication) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
//        Instant moment = Instant.now();
//        SecretKey cipher = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

//        return Jwts.builder()
//                .setSubject(user.getUsername())
//                .setIssuedAt(Date.from(moment))
//                .setExpiration(Date.from(moment.plus(1, ChronoUnit.HOURS)))
//                .signWith(cipher)
//                .compact();

        return "%s@%s".formatted(user.getUsername(), new Date());
    }

    /**
     * Validates that the string in question is a present session-token
     * in the database and the linked account has no reasons to be inactive.
     * @param token string being validated.
     * @return  true if the string is an active session token
     * and the linked account is not inactive.
     */
    public boolean validateToken(String token) {
        return false;
    }

    /**
     * Attempts to authenticate the passed {@link Authentication} object, returning a
     * fully populated <code>Authentication</code> object (including granted authorities)
     * if successful.
     * <p>
     * An <code>AuthenticationManager</code> must honour the following contract concerning
     * exceptions:
     * <ul>
     * <li>A {@link DisabledException} must be thrown if an account is disabled and the
     * <code>AuthenticationManager</code> can test for this state.</li>
     * <li>A {@link LockedException} must be thrown if an account is locked and the
     * <code>AuthenticationManager</code> can test for account locking.</li>
     * <li>A {@link BadCredentialsException} must be thrown if incorrect credentials are
     * presented. Whilst the above exceptions are optional, an
     * <code>AuthenticationManager</code> must <B>always</B> test credentials.</li>
     * </ul>
     * Exceptions should be tested for and if applicable thrown in the order expressed
     * above (i.e. if an account is disabled or locked, the authentication request is
     * immediately rejected and the credentials testing process is not performed). This
     * prevents credentials being tested against disabled or locked accounts.
     *
     * @param authentication the authentication request object
     * @return a fully authenticated object including credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }
}
