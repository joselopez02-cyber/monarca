package com.cinemamonarca.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── Frontend estático ──────────────────────────────────────
                        .requestMatchers("/", "/index.html", "/*.html").permitAll()
                        // ── Swagger UI ─────────────────────────────────────────────
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // ── Auth pública ───────────────────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()
                        // ── Lectura pública de cartelera ───────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/funciones/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/salas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sillas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cines/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sucursales/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gestores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pagos/metodos").permitAll()
                        // ── Reservas, clientes y transacciones requieren login ─────
                        .requestMatchers("/api/reservas/**").authenticated()
                        .requestMatchers("/api/transacciones/**").authenticated()
                        .requestMatchers("/api/clientes/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/pagos/**").authenticated()
                        // ── CRUD solo ADMIN ────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/funciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/funciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/funciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/salas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/sucursales/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sucursales/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/gestores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/gestores/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}