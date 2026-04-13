package cinema.monarca.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Al usar "/" se adapta automáticamente a Localhost o a Railway
        Server server = new Server();
        server.setUrl("/");
        server.setDescription("Servidor de Cinema Monarca");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Cinema Monarca API")
                        .version("1.0")
                        .description("Documentación oficial de Cinema Monarca"));
    }
}