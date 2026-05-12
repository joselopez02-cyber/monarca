package cinema.monarca.repository;
import cinema.monarca.model.Cine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CineRepository extends JpaRepository<Cine, Long> {}
