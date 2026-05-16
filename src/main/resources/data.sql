-- =============================================
-- Cinema Monarca — Datos semilla v3
-- =============================================

-- ── Usuarios (admin → monarca123 | usuario → monarca123) ────────
INSERT IGNORE INTO usuario (username, email, password_hash, rol) VALUES
('admin',   'admin@monarca.co',   '$2a$12$XRAVvAi4ckSjhajv3QXgMeHiN5o0Z4/TK2h/3XDxD5MtWknl3VE/6', 'ADMIN'),
('usuario', 'usuario@monarca.co', '$2a$12$XRAVvAi4ckSjhajv3QXgMeHiN5o0Z4/TK2h/3XDxD5MtWknl3VE/6', 'USER'),
('1',       '1@monarca.co',       '$2a$12$RF0eGB.ALJb5OSn0Ssxy7uhB/VJSnTuWbxeUqXmQ4ng.VU.6nTz8C',  'ADMIN');

-- ── Cines y sucursales (Cali) ─────────────────
INSERT IGNORE INTO cine (cine_id, nombre_del_cine, cine_cont) VALUES
(1, 'Cinema Monarca Centro', '+57 2 8901234'),
(2, 'Cinema Monarca Norte',  '+57 2 8905678');

INSERT IGNORE INTO sucursal (bran_id, bran_location, cine_id) VALUES
(1, 'Calle 10 # 5-20, Centro, Cali',          1),
(2, 'Av. Simón Bolívar # 45-12, Norte, Cali', 2);

INSERT IGNORE INTO gestor (manager_id, manager_details, sucursal_id) VALUES
(1, 'Carlos Ruiz — Gerente Senior', 1),
(2, 'María López — Gerente Norte',  2);

-- ── Clientes demo ────────────────────────────
INSERT IGNORE INTO cliente (cust_id, nombre_cliente, cust_age, direccion_cliente, numero_cliente) VALUES
(1, 'Juan Pérez', 30, 'Cra 5 # 10-20, Cali', '3001234567'),
(2, 'Ana García', 25, 'Cll 15 # 8-40, Cali',  '3109876543');

-- ── Salas ─────────────────────────────────────
INSERT IGNORE INTO sala (sala_id, nombre, tipo, capacidad, sucursal_id) VALUES
-- Sucursal Centro (1)
( 1,'Sala PRO 1','PRO',   20,1),( 2,'Sala PRO 2','PRO',   20,1),
( 3,'Sala PRO 3','PRO',   20,1),( 4,'Sala PRO 4','PRO',   20,1),
( 5,'Sala PRO 5','PRO',   20,1),( 6,'Sala PRO 6','PRO',   20,1),
( 7,'Sala 3D 1', 'TRES_D',30,1),( 8,'Sala 3D 2', 'TRES_D',30,1),
( 9,'Sala 2D 1', 'DOS_D', 50,1),(10,'Sala 2D 2', 'DOS_D', 50,1),
-- Sucursal Norte (2)
(11,'Sala PRO 1','PRO',   20,2),(12,'Sala PRO 2','PRO',   20,2),
(13,'Sala PRO 3','PRO',   20,2),(14,'Sala PRO 4','PRO',   20,2),
(15,'Sala PRO 5','PRO',   20,2),(16,'Sala PRO 6','PRO',   20,2),
(17,'Sala 3D 1', 'TRES_D',30,2),(18,'Sala 3D 2', 'TRES_D',30,2),
(19,'Sala 2D 1', 'DOS_D', 50,2),(20,'Sala 2D 2', 'DOS_D', 50,2);

-- ── Películas ─────────────────────────────────
INSERT IGNORE INTO pelicula (movie_id, nombre, descripcion, duracion_min, genero) VALUES
(1,'Oppenheimer',
   'La historia del padre de la bomba atómica contada por Christopher Nolan.',
   180,'DRAMA'),
(2,'Dune: Parte Dos',
   'Paul Atreides se une a los Fremen para vengar a su familia.',
   166,'CIENCIA_FICCION'),
(3,'Avatar: El Camino del Agua',
   'La épica continuación de James Cameron regresa al mundo de Pandora.',
   192,'CIENCIA_FICCION'),
(4,'Spider-Man: No Way Home',
   'El héroe arácnido enfrenta su mayor desafío entre multiversos.',
   148,'ACCION'),
(5,'El Rey León (Clásico)',
   'La historia de Simba regresa a la gran pantalla en proyección 2D.',
   88,'ANIMACION'),
(6,'Inception',
   'Un ladrón especializado en el robo de secretos dentro del subconsciente.',
   148,'THRILLER');

-- ── Funciones ─────────────────────────────────
INSERT IGNORE INTO funcion
    (funcion_id, movie_id, sala_id, fecha, hora_inicio, precio_boleto, capacidad_total, asientos_disponibles)
VALUES
(1, 1, 1, '2026-06-15', '15:00', 28000, 20, 20),
(2, 1, 2, '2026-06-15', '19:00', 28000, 20, 20),
(3, 2, 3, '2026-06-16', '18:00', 28000, 20, 20),
(4, 2, 4, '2026-06-16', '21:00', 28000, 20, 20),
(5, 3, 7, '2026-06-20', '18:30', 22000, 30, 30),
(6, 3, 8, '2026-06-20', '21:30', 22000, 30, 30),
(7, 4, 7, '2026-06-21', '16:00', 22000, 30, 30),
(8, 5, 9, '2026-06-25', '14:00', 16000, 50, 50),
(9,  6,10, '2026-06-26', '16:00', 16000, 50, 50),
(10, 6,10, '2026-06-26', '20:00', 16000, 50, 50);