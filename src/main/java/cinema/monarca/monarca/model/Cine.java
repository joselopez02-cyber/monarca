package cinema.monarca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cine_id")
    private Long cineId;

    @NotBlank
    @Column(name = "nombre_del_cine", nullable = false)
    private String nombreDelCine;

    @Column(name = "cine_cont")
    private String cineCont;
}
