package com.sa1zer.simpleauth.service.jwt;

import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.service.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.jsonwebtoken.Jwts.SIG.HS512;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String TOKEN_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;

    private SecretKey key;

    @Value("${jwt.duration}")
    private long durationToken;

    public String generateToken(User user) {
        Date now = new Date(System.currentTimeMillis());
        Date expiredTime = new Date(now.getTime() + durationToken * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id",  user.getId());
        claims.put("login",  user.getLogin());

        return TOKEN_PREFIX + Jwts.builder()
                .issuedAt(now)
                .expiration(expiredTime)
                .claims(claims)
                .signWith(getPrivateKey())
                .compact();
    }

    public Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return Optional.of(authHeader.substring(TOKEN_PREFIX.length()));
        }

        return Optional.empty();
    }

    public Authentication getAuthentication(String token) {
        var login = getLoginFromToken(token);
        var userDetails = userDetailsService.loadUserByUsername(login);

        return new UsernamePasswordAuthenticationToken(login, "", userDetails.getAuthorities());
    }

    public boolean isTokenValid(String token) {
        var claimsJws = parseToken(token);

        try {
            return claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException ignored) {
        }
        return false;
    }

    private String getLoginFromToken(String token) {
        return (String) parseToken(token)
                .getPayload()
                .get("login");
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getPrivateKey())
                .build().parseSignedClaims(token);
    }

    private SecretKey getPrivateKey() {
        if(key == null) {
            key = HS512.key().build();
        }
        return key;
    }
}
