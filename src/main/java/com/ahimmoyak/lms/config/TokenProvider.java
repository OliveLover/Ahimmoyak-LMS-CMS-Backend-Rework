package com.ahimmoyak.lms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.security.oauth2.resource-server.jwt.issuer-uri}")
    private String issuerUri;

    private final JwtDecoder jwtDecoder;

    public JwtDecoder accessTokenDecoder() {
        return NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
    }

    public Authentication getAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        String username = jwt.getClaim("username");

        List<String> groups = jwt.getClaimAsStringList("cognito:groups");

        List<SimpleGrantedAuthority> authorities = groups != null ?
                groups.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                : List.of();

        return new JwtAuthenticationToken(jwt, authorities, username);
    }

}
