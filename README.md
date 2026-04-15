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
        US[UserService]
    end

    subgraph Domain [Capa de Dominio]
        MR[MovieRepository]
        UR[UserRepository]
        M((Movie Entity))
        U((User Entity))
    end

    %% Relaciones de flujo lógico corregidas
    MC --> MS
    MS --> MR
    MR --> M
    %% Nueva conexión del UserService
    UC --> US
    US --> UR
    UR --> U
    AC --> SC
