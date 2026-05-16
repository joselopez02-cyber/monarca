package com.cinemamonarca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("Cinema Monarca API")
                .version("1.0")
                .description("API REST para el sistema de taquilla Cinema Monarca. " +
                    "Para autenticarte: usa POST /api/auth/login con tu usuario y contraseña, " +
                    "copia el token recibido y pégalo en el botón 'Authorize' arriba a la derecha.")
                .contact(new Contact()
                    .name("Cinema Monarca")
                    .email("admin@monarca.co")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Ingresa el token JWT obtenido del endpoint /api/auth/login")));
    }
}
