package cinema.monarca.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en tus controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitado para facilitar pruebas en Postman/Swagger
                .authorizeHttpRequests(auth -> auth
                        // PERMITIMOS SWAGGER Y API DOCS SIN AUTENTICACIÓN
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // PERMITIMOS VER LAS PELÍCULAS A TODO EL MUNDO
                        .requestMatchers("/api/peliculas").permitAll()
                        // EL RESTO DE LAS APIS REQUIEREN LOGIN
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // Login básico (usuario/contraseña)
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Usuario ADMIN para que puedas CREAR, EDITAR y BORRAR películas
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        // Usuario USER solo para ver (LISTAR)
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user123")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}