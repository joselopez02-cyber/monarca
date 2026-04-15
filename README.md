```mermaid
graph TD
    subgraph Infrastructure [Capa de Infraestructura]
        AC[AuthController]
        MC[MovieController]
        UC[UserController]
        HC[HomeController]
        SC[SecurityConfig]
        OC[OpenApiConfig]
        CC[CorsConfig]
    end

    subgraph Services [Capa de Negocio]
        MS[MovieService]
    end

    subgraph Domain [Capa de Dominio]
        MR[MovieRepository]
        UR[UserRepository]
        M((Movie Entity))
        U((User Entity))
    end

    %% Relaciones de flujo lógico
    MC --> MS
    MS --> MR
    MR --> M
    UC --> UR
    UR --> U
    AC --> SC