package ru.codeportfolio.tasktracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;
import ru.codeportfolio.tasktracker.dto.jwt.JwtClaimsDto;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtService {

    @Value("${secret-key}")
    private String secretKey;


    public JwtAuthenticationDto generateJwtToken(String email, Collection<? extends GrantedAuthority> authorities, Long id) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto(
                generateAuthToken(email, authorities, id)
        );
        return jwtDto;
    }


    public JwtClaimsDto getJwtClaimsDtoFromToken(String token) {
        Claims claims = getClaims(token);
        List<String> roles = claims.get("authorities", List.class);
        return new JwtClaimsDto(
                claims.get("id", Long.class),
                claims.getSubject(),
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }
    }


    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateAuthToken(String email, Collection<? extends GrantedAuthority> authorities, Long id) {
        Date date = getDateExpireToken(16);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(email)
                .claim("authorities", roles)
                .claim("id", id)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private static @NonNull Date getDateExpireToken(int hours) {
        return Date.from(LocalDateTime.now().plusHours(hours).atZone(ZoneId.systemDefault()).toInstant());
    }


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
