package cinema.monarca.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        // 1. Acceso libre a Consolas y Documentación (Asegurando rutas de H2)
                        .requestMatchers("/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Reglas de Películas
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**").permitAll()
                        .requestMatchers("/api/peliculas/**").hasRole("ADMIN")

                        // 3. Reglas de Usuarios y Auth
                        .requestMatchers(HttpMethod.POST, "/api/users/registro").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 4. Todo lo demás requiere login
                        .anyRequest().authenticated()
                )
                // CONFIGURACIÓN DE LOGOUT UNIFICADA
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.getWriter().write("SESION CERRADA EXITOSAMENTE EN CINEMA MONARCA");
                        })
                        .permitAll()
                )
                // CONFIGURACIÓN PARA RAILWAY Y H2
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // Permite ver H2 dentro del navegador
                        .contentSecurityPolicy(csp -> csp.policyDirectives("upgrade-insecure-requests;")) // Evita bloqueos por HTTPS en Railway
                )
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("admin123")
                        .roles("ADMIN")
                        .build(),
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("user123")
                        .roles("USER")
                        .build()
        );
    }
}