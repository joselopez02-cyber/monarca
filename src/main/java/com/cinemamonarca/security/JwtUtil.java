package com.cinemamonarca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    private SecretKey cachedKey;

    /**
     * Fallo rapido intencional: si APP_JWT_SECRET no esta configurada en Azure
     * (o tiene menos de 32 caracteres), el servidor NO arranca.
     * Esto evita despliegues silenciosos con seguridad rota.
     *
     * ACCION REQUERIDA EN AZURE:
     *   App Service → Configuration → Application settings → + New application setting
     *     Name:  APP_JWT_SECRET
     *     Value: M0n@rca$CinemaS3cur3K3y!2026#AzurePRD   (42 chars, pasa la validacion)
     */
    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                "[Cinema Monarca] ERROR: La variable APP_JWT_SECRET no esta configurada. " +
                "Definela en Azure → App Service → Configuration antes de desplegar."
            );
        }
        if (secret.length() < 32) {
            throw new IllegalStateException(
                "[Cinema Monarca] ERROR: APP_JWT_SECRET debe tener minimo 32 caracteres."
            );
        }
        cachedKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un JWT firmado con subject=username y claim "rol".
     *
     * IMPORTANTE: el claim se llama "rol" (sin 'e').
     * El filtro JwtAuthFilter debe leer este mismo claim y construir
     * la autoridad como "ROLE_" + rol para que ROLE_ADMIN funcione:
     *
     *   String rol = jwtUtil.getRol(token);
     *   List<GrantedAuthority> authorities =
     *       List.of(new SimpleGrantedAuthority("ROLE_" + rol));
     *
     * Si el prefijo "ROLE_" no se agrega en el filtro, el check
     * auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
     * siempre devolvera false y el admin recibira 403.
     */
    public String generate(String username, String rol) {
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(cachedKey)
                .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** Devuelve el valor crudo del claim "rol", ej: "ADMIN" o "USER". */
    public String getRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(cachedKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}