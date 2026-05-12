package com.cinemamonarca.repository;
import com.cinemamonarca.model.Cine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CineRepository extends JpaRepository<Cine, Long> {}
