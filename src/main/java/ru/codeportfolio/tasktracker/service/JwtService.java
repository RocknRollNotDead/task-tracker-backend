package ru.codeportfolio.tasktracker.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.codeportfolio.tasktracker.dto.jwt.JwtAuthenticationDto;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtService {

    @Value("${secret-key}") // todo
    private String secretKey;

    private JwtAuthenticationDto generateAuthToken(String email){
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto(
                generateJwtToken(email),
                generateRefreshToken(email)
        );
        return jwtDto;
    }


    private JwtAuthenticationDto refreshToken(String email, String refreshToken){
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto(
                generateJwtToken(email),
                refreshToken
        );
        return jwtDto;
    }


    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }


    public boolean validateJwtToken(String token){
        try {

            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e){
            log.error("Expired Jwt exception", e);
        } catch (UnsupportedJwtException e){
            log.error("Unsupported Jwt exception", e);
        } catch (MalformedJwtException e){
            log.error("Malformed Jwt exception", e);

        } catch (SecurityException e) {
            log.error("Security exception", e);
        } catch (Exception e) {
            log.error("Unknown exception", e);
        }

        return false;
    }



    private String generateJwtToken(String email){
        Date date = Date.from(LocalDateTime.now().plusHours(8).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email){
        Date date = Date.from(LocalDateTime.now().plusDays(2).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }


    private SecretKey getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
