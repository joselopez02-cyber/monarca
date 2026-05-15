package com.cinemamonarca.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protección anti-fuerza-bruta para el endpoint de login.
 *
 * Reglas:
 *  - Máximo MAX_ATTEMPTS intentos fallidos por IP en una ventana de WINDOW_SECONDS.
 *  - Al superar el límite, la IP queda bloqueada BLOCK_SECONDS segundos.
 *  - Un login exitoso reinicia el contador de esa IP.
 *  - Limpieza automática lazy: entradas antiguas se purgan al consultar.
 */
@Component
public class LoginRateLimiter {

    private static final int  MAX_ATTEMPTS    = 5;
    private static final long WINDOW_SECONDS  = 60;   // ventana de conteo
    private static final long BLOCK_SECONDS   = 300;  // 5 min bloqueado

    private record Bucket(int attempts, Instant windowStart, Instant blockedUntil) {}

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /** @return true si la IP está bloqueada y NO debe continuar. */
    public boolean isBlocked(String ip) {
        Bucket b = buckets.get(ip);
        if (b == null) return false;
        if (b.blockedUntil() != null && Instant.now().isBefore(b.blockedUntil())) {
            return true;
        }
        return false;
    }

    /** Registra un intento fallido. Bloquea la IP si supera MAX_ATTEMPTS. */
    public void registerFailure(String ip) {
        Instant now = Instant.now();
        buckets.compute(ip, (k, b) -> {
            if (b == null || now.isAfter(b.windowStart().plusSeconds(WINDOW_SECONDS))) {
                // Nueva ventana
                return new Bucket(1, now, null);
            }
            int newAttempts = b.attempts() + 1;
            Instant blocked = newAttempts >= MAX_ATTEMPTS
                    ? now.plusSeconds(BLOCK_SECONDS)
                    : b.blockedUntil();
            return new Bucket(newAttempts, b.windowStart(), blocked);
        });
        // Purga lazy: elimina entradas expiradas ocasionalmente
        if (Math.random() < 0.05) purge();
    }

    /** Registra un login exitoso: reinicia el bucket de esa IP. */
    public void registerSuccess(String ip) {
        buckets.remove(ip);
    }

    /** Segundos restantes de bloqueo (para el header Retry-After). */
    public long secondsUntilUnblocked(String ip) {
        Bucket b = buckets.get(ip);
        if (b == null || b.blockedUntil() == null) return 0;
        long secs = Instant.now().until(b.blockedUntil(), java.time.temporal.ChronoUnit.SECONDS);
        return Math.max(0, secs);
    }

    private void purge() {
        Instant now = Instant.now();
        buckets.entrySet().removeIf(e -> {
            Bucket b = e.getValue();
            // Eliminar si la ventana ya expiró Y (no está bloqueado O el bloqueo ya pasó)
            boolean windowExpired = now.isAfter(b.windowStart().plusSeconds(WINDOW_SECONDS));
            boolean blockExpired  = b.blockedUntil() == null || now.isAfter(b.blockedUntil());
            return windowExpired && blockExpired;
        });
    }
}
