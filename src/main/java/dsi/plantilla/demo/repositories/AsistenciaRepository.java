package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.models.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    // Tarea 3: Verificar si el trabajador ya tiene registro en esa fecha
    boolean existsByTrabajadorAndFecha(Trabajador trabajador, LocalDate fecha);
}