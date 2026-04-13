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
        // Servidor de Producción (El que está en tu captura de Railway)
        Server productionServer = new Server();
        productionServer.setUrl("https://oasis-production-c8f5.up.railway.app");
        productionServer.setDescription("Servidor de Producción en Railway");

        // Servidor Local (Para cuando trabajas en IntelliJ)
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Servidor Local");

        return new OpenAPI()
                .servers(List.of(productionServer, localServer))
                .info(new Info()
                        .title("Monarca Cinema API")
                        .version("1.0")
                        .description("Conexión directa entre Swagger y la base de datos de Railway"));
    }
}