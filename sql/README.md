# Cinema Monarca — SQL Scripts v3

## Orden de ejecución

```bash
mysql -u root -p < 01_schema.sql
mysql -u root -p < 02_seed.sql
```

## Cambios respecto a v2

- `sala.tipo` ENUM: `'3D','2D'` → `'TRES_D','DOS_D'` (coincide con Java `EnumType.STRING`)
- `pelicula`: eliminados `fecha`, `hora_espectaculo`, `capacidad_total`, `asientos_disponibles`, `precio_boleto`, `sala_id`
- Nueva tabla `funcion`: une `pelicula` + `sala` con fecha/hora/precio/capacidad
- `silla`: FK `pelicula_id` → `funcion_id`
- `reserva`: FK `movie_id` y `cine_id` → `funcion_id`
