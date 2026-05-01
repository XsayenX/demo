package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    boolean existsByDui(String dui);
    boolean existsByNss(String nss);
}