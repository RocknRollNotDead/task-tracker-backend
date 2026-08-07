package ru.codeportfolio.tasktracker.dto.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record JwtClaimsDto(
        Long id,
        String email,
        Collection<? extends GrantedAuthority> authorities

) {
}
