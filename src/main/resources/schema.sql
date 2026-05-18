-- =============================================
-- Cinema Monarca — Schema v4 (PostgreSQL)
-- =============================================

-- Tipos ENUM (IF NOT EXISTS para evitar error si ya existen)
DO $$ BEGIN CREATE TYPE rol_usuario   AS ENUM ('USER','ADMIN');        EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE tipo_sala     AS ENUM ('PRO','TRES_D','DOS_D'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE estado_reserva AS ENUM ('PENDIENTE','CONFIRMADA','CANCELADA'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE estado_silla  AS ENUM ('DISPONIBLE','OCUPADA','RESERVADA');    EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE tipo_pago     AS ENUM ('TARJETA_CREDITO','TARJETA_DEBITO','NEQUI','PSE'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE estado_pago   AS ENUM ('SIMULADO','APROBADO','RECHAZADO');     EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Usuarios / Auth
CREATE TABLE IF NOT EXISTS usuario (
    usuario_id    BIGSERIAL PRIMARY KEY,
    username      VARCHAR(80)  NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol           rol_usuario  NOT NULL DEFAULT 'USER',
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Cine y Sucursales
CREATE TABLE IF NOT EXISTS cine (
    cine_id         BIGSERIAL PRIMARY KEY,
    nombre_del_cine VARCHAR(255) NOT NULL,
    cine_cont       VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS sucursal (
    bran_id       BIGSERIAL PRIMARY KEY,
    bran_location VARCHAR(255),
    cine_id       BIGINT,
    FOREIGN KEY (cine_id) REFERENCES cine(cine_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS gestor (
    manager_id      BIGSERIAL PRIMARY KEY,
    manager_details VARCHAR(500),
    sucursal_id     BIGINT,
    FOREIGN KEY (sucursal_id) REFERENCES sucursal(bran_id) ON DELETE SET NULL
);

-- Clientes
CREATE TABLE IF NOT EXISTS cliente (
    cust_id           BIGSERIAL PRIMARY KEY,
    nombre_cliente    VARCHAR(255) NOT NULL,
    cust_age          INT,
    direccion_cliente VARCHAR(500),
    numero_cliente    VARCHAR(50)
);

-- Sala (✅ filas y columnas agregados)
CREATE TABLE IF NOT EXISTS sala (
    sala_id     BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    tipo        tipo_sala    NOT NULL,
    capacidad   INT          NOT NULL,
    filas       INT,
    columnas    INT,
    sucursal_id BIGINT,
    FOREIGN KEY (sucursal_id) REFERENCES sucursal(bran_id) ON DELETE SET NULL
);

-- Peliculas
CREATE TABLE IF NOT EXISTS pelicula (
    movie_id      BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(255) NOT NULL,
    descripcion   TEXT,
    duracion_min  INT,
    genero        VARCHAR(50),
    clasificacion VARCHAR(10)
);

-- Funciones (✅ fecha_inicio y fecha_fin agregados)
CREATE TABLE IF NOT EXISTS funcion (
    funcion_id           BIGSERIAL PRIMARY KEY,
    movie_id             BIGINT           NOT NULL,
    sala_id              BIGINT           NOT NULL,
    fecha                VARCHAR(20)      NOT NULL,
    hora_inicio          VARCHAR(10)      NOT NULL,
    fecha_inicio         VARCHAR(20),
    fecha_fin            VARCHAR(20),
    precio_boleto        DOUBLE PRECISION NOT NULL DEFAULT 16000,
    capacidad_total      INT              NOT NULL,
    asientos_disponibles INT              NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES pelicula(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (sala_id)  REFERENCES sala(sala_id)      ON DELETE CASCADE
);

-- Reservas (✅ campos snapshot agregados)
CREATE TABLE IF NOT EXISTS reserva (
    res_code       BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(255),
    tiempo         VARCHAR(50),
    fecha          VARCHAR(50),
    cont_num       VARCHAR(100),
    estado         estado_reserva   NOT NULL DEFAULT 'CONFIRMADA',
    funcion_id     BIGINT,
    cust_id        BIGINT,
    snap_pelicula  VARCHAR(255),
    snap_sala      VARCHAR(100),
    snap_tipo_sala VARCHAR(20),
    snap_fecha     VARCHAR(20),
    snap_hora      VARCHAR(10),
    snap_precio    DOUBLE PRECISION,
    snap_sillas    VARCHAR(500),
    snap_total     DOUBLE PRECISION,
    FOREIGN KEY (funcion_id) REFERENCES funcion(funcion_id) ON DELETE SET NULL,
    FOREIGN KEY (cust_id)    REFERENCES cliente(cust_id)    ON DELETE SET NULL
);

-- Sillas
CREATE TABLE IF NOT EXISTS silla (
    silla_id   BIGSERIAL PRIMARY KEY,
    fila       VARCHAR(2)   NOT NULL,
    numero     INT          NOT NULL,
    estado     estado_silla NOT NULL DEFAULT 'DISPONIBLE',
    funcion_id BIGINT       NOT NULL,
    reserva_id BIGINT,
    UNIQUE (funcion_id, fila, numero),
    FOREIGN KEY (funcion_id) REFERENCES funcion(funcion_id) ON DELETE CASCADE,
    FOREIGN KEY (reserva_id) REFERENCES reserva(res_code)   ON DELETE SET NULL
);

-- Transacciones / Pagos
CREATE TABLE IF NOT EXISTS transaccion (
    trans_no     BIGSERIAL PRIMARY KEY,
    tipo_pago    tipo_pago    NOT NULL,
    pago_total   DECIMAL(10,2),
    estado_pago  estado_pago  NOT NULL DEFAULT 'SIMULADO',
    referencia   VARCHAR(50),
    ultimos4     CHAR(4),
    fecha_inicio VARCHAR(50),
    fecha_final  VARCHAR(50),
    fecha_trans  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    cust_id      BIGINT,
    res_code     BIGINT,
    FOREIGN KEY (cust_id)  REFERENCES cliente(cust_id)  ON DELETE SET NULL,
    FOREIGN KEY (res_code) REFERENCES reserva(res_code) ON DELETE SET NULL
);

-- ✅ ALTER TABLE para tablas ya existentes en Aiven
-- (IF NOT EXISTS evita error si ya se corrió antes)
ALTER TABLE funcion  ADD COLUMN IF NOT EXISTS fecha_inicio         VARCHAR(20);
ALTER TABLE funcion  ADD COLUMN IF NOT EXISTS fecha_fin            VARCHAR(20);
ALTER TABLE sala     ADD COLUMN IF NOT EXISTS filas                INT;
ALTER TABLE sala     ADD COLUMN IF NOT EXISTS columnas             INT;
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_pelicula        VARCHAR(255);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_sala            VARCHAR(100);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_tipo_sala       VARCHAR(20);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_fecha           VARCHAR(20);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_hora            VARCHAR(10);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_precio          DOUBLE PRECISION;
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_sillas          VARCHAR(500);
ALTER TABLE reserva  ADD COLUMN IF NOT EXISTS snap_total           DOUBLE PRECISION;