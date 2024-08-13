package org.silluck.domain.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.silluck.domain.domain.common.UserType;
import org.silluck.domain.domain.common.UserVo;
import org.silluck.domain.util.Aes256Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationProvider {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    final private long tokenValidTime = 1000L * 60 * 60 * 24;

    public String createToken(String userPk, Long id, UserType userType) {
        Claims claims = Jwts.claims()
                .setSubject(Aes256Util.encrypt(userPk)).setId(Aes256Util.encrypt(id.toString()));
        claims.put("roles", userType);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            return validateTokenInternal(jwtToken);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateTokenInternal(String jwtToken) {
        try {
            Claims claims = parsedClaims(jwtToken);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Failed to validate jwtToken: {}", e.getMessage());
            return false;
        }
    }

    private Claims parsedClaims(String token) {
        try {
            Key key2 = Keys.hmacShaKeyFor(secretKey.getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key2)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 일딴 유저 id와 email 기반으로 동작
    public UserVo getUserVo(String token) {
        Claims claims = parsedClaims(token);
        return new UserVo(
                Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(claims.getId())))
                , Aes256Util.decrypt(claims.getSubject()));
    }

    // JWT에서 "roles" 클레임을 추출
    public String getRoleFromToken(String token) {
        Claims claims = parsedClaims(token);
        return (String) claims.get("roles");
    }
}
