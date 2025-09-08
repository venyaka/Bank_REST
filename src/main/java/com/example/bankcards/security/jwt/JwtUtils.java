package com.example.bankcards.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthorizeException;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.time-expiration}")
    private Long jwtExpirationTime;

    @Value("${jwt.token-refresh.time-expiration}")
    private Long jwtRefreshExpirationTime;

    private final UserRepository userRepository;

    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        claims.put("typeToken", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .signWith(key).compact();

    }

    public String generateRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("refreshToken", user.getRefreshToken());
        claims.put("typeToken", "refresh");
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .signWith(key).compact();

    }

    public boolean validateRefreshToken(String token) {
        try {
            validateToken(token);
        } catch (AuthorizeException exception) {
            throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN.getMessage() + " " + exception.getMessage(), BadRequestError.NOT_CORRECT_REFRESH_TOKEN.name());
        }
        Optional<User> optionalUser = userRepository.findByEmail(getUserEmailFromToken(token));
        if (optionalUser.isEmpty()) {
            throw new BadRequestException(BadRequestError.NOT_CORRECT_REFRESH_TOKEN);
        }
        User user = optionalUser.get();

        String correctRefreshToken = user.getRefreshToken();
        String givenRefreshToken = getRefreshStringFromToken(token);
        return correctRefreshToken.equals(givenRefreshToken);

    }


    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException |
                 DecodingException ex) {
            throw new AuthorizeException(AuthorizedError.NOT_CORRECT_TOKEN);
        } catch (ExpiredJwtException ex) {
            throw new AuthorizeException(AuthorizedError.TOKEN_WAS_EXPIRED);
        }
    }

    public String getUserEmailFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }

    public String getRefreshStringFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return (String) claims.get("refreshToken");
    }

    public String getTypeTokenFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return (String) claims.get("typeToken");
    }

    public String generateRandomSequence() {
        return RandomStringUtils.randomAlphanumeric(50);
    }

}
