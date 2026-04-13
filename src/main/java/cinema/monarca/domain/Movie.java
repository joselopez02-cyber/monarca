package cinema.monarca.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "peliculas")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String genero;
    private int duracionMinutos;
    private double precioBoleto;
}