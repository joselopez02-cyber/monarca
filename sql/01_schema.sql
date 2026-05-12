-- =============================================
-- Cinema Monarca — Schema v3
-- Ejecutar en orden: 01_schema.sql → 02_seed.sql
-- =============================================

CREATE DATABASE IF NOT EXISTS cinema_monarca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinema_monarca;

-- ──────────────────────────────────────────────
-- Usuarios / Auth
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuario (
                                       usuario_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       username      VARCHAR(80)  NOT NULL UNIQUE,
                                       email         VARCHAR(150) NOT NULL UNIQUE,
                                       password_hash VARCHAR(255) NOT NULL,
                                       rol           ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
                                       activo        TINYINT(1) NOT NULL DEFAULT 1,
                                       created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ──────────────────────────────────────────────
-- Cine y Sucursales
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cine (
                                    cine_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    nombre_del_cine VARCHAR(255) NOT NULL,
                                    cine_cont       VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS sucursal (
                                        bran_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        bran_location VARCHAR(255),
                                        cine_id       BIGINT,
                                        FOREIGN KEY (cine_id) REFERENCES cine(cine_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS gestor (
                                      manager_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      manager_details VARCHAR(500),
                                      sucursal_id     BIGINT,
                                      FOREIGN KEY (sucursal_id) REFERENCES sucursal(bran_id) ON DELETE SET NULL
);

-- ──────────────────────────────────────────────
-- Clientes
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cliente (
                                       cust_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       nombre_cliente    VARCHAR(255) NOT NULL,
                                       cust_age          INT,
                                       direccion_cliente VARCHAR(500),
                                       numero_cliente    VARCHAR(50)
);

-- ──────────────────────────────────────────────
-- Sala
-- IMPORTANTE: ENUM usa 'PRO','TRES_D','DOS_D' para que coincida con
-- los valores que Hibernate almacena (EnumType.STRING en Java).
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sala (
                                    sala_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    nombre       VARCHAR(100) NOT NULL,
                                    tipo         ENUM('PRO','TRES_D','DOS_D') NOT NULL,
                                    capacidad    INT NOT NULL,
                                    filas        INT,
                                    columnas     INT,
                                    sucursal_id  BIGINT,
                                    FOREIGN KEY (sucursal_id) REFERENCES sucursal(bran_id) ON DELETE SET NULL
);

-- ──────────────────────────────────────────────
-- Películas — solo catálogo, sin campos de función
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pelicula (
                                        movie_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nombre         VARCHAR(255) NOT NULL,
                                        descripcion    TEXT,
                                        duracion_min   INT,
                                        genero         VARCHAR(50),
                                        clasificacion  VARCHAR(10)
);

-- ──────────────────────────────────────────────
-- Funciones (proyecciones)
-- Una Pelicula puede tener N Funciones en distintas salas y horarios.
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS funcion (
                                       funcion_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       movie_id             BIGINT NOT NULL,
                                       sala_id              BIGINT NOT NULL,
                                       fecha                VARCHAR(20)  NOT NULL,
                                       hora_inicio          VARCHAR(10)  NOT NULL,
                                       precio_boleto        DOUBLE       NOT NULL DEFAULT 16000,
                                       capacidad_total      INT          NOT NULL,
                                       asientos_disponibles INT          NOT NULL,
                                       FOREIGN KEY (movie_id) REFERENCES pelicula(movie_id) ON DELETE CASCADE,
                                       FOREIGN KEY (sala_id)  REFERENCES sala(sala_id)      ON DELETE CASCADE
);

-- ──────────────────────────────────────────────
-- Reservas — apuntan a Funcion, no a Pelicula
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reserva (
                                       res_code   BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       nombre     VARCHAR(255),
                                       tiempo     VARCHAR(50),
                                       fecha      VARCHAR(50),
                                       cont_num   VARCHAR(100),
                                       estado     ENUM('PENDIENTE','CONFIRMADA','CANCELADA') NOT NULL DEFAULT 'CONFIRMADA',
                                       funcion_id BIGINT,
                                       cust_id    BIGINT,
                                       FOREIGN KEY (funcion_id) REFERENCES funcion(funcion_id) ON DELETE SET NULL,
                                       FOREIGN KEY (cust_id)    REFERENCES cliente(cust_id)    ON DELETE SET NULL
);

-- ──────────────────────────────────────────────
-- Sillas — asiento por función, no por película
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS silla (
                                     silla_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     fila       VARCHAR(2)  NOT NULL,
                                     numero     INT         NOT NULL,
                                     estado     ENUM('DISPONIBLE','OCUPADA','RESERVADA') NOT NULL DEFAULT 'DISPONIBLE',
                                     funcion_id BIGINT NOT NULL,
                                     reserva_id BIGINT,
                                     UNIQUE KEY uk_silla (funcion_id, fila, numero),
                                     FOREIGN KEY (funcion_id) REFERENCES funcion(funcion_id) ON DELETE CASCADE,
                                     FOREIGN KEY (reserva_id) REFERENCES reserva(res_code)   ON DELETE SET NULL
);

-- ──────────────────────────────────────────────
-- Transacciones / Pagos (simulados)
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS transaccion (
                                           trans_no    BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           tipo_pago   ENUM('TARJETA_CREDITO','TARJETA_DEBITO','NEQUI','PSE') NOT NULL,
                                           pago_total  DECIMAL(10,2),
                                           estado_pago ENUM('SIMULADO','APROBADO','RECHAZADO') NOT NULL DEFAULT 'SIMULADO',
                                           referencia  VARCHAR(50),
                                           ultimos4    CHAR(4),
                                           fecha_inicio VARCHAR(50),
                                           fecha_final  VARCHAR(50),
                                           fecha_trans  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           cust_id     BIGINT,
                                           res_code    BIGINT,
                                           FOREIGN KEY (cust_id)  REFERENCES cliente(cust_id)  ON DELETE SET NULL,
                                           FOREIGN KEY (res_code) REFERENCES reserva(res_code) ON DELETE SET NULL
);

-- ──────────────────────────────────────────────
-- v4: Campos de perfil en usuario
-- ──────────────────────────────────────────────
ALTER TABLE usuario
  ADD COLUMN IF NOT EXISTS nombre_completo  VARCHAR(255),
  ADD COLUMN IF NOT EXISTS cedula           VARCHAR(20) UNIQUE,
  ADD COLUMN IF NOT EXISTS telefono         VARCHAR(30),
  ADD COLUMN IF NOT EXISTS direccion        VARCHAR(500),
  ADD COLUMN IF NOT EXISTS fecha_nacimiento VARCHAR(10);
