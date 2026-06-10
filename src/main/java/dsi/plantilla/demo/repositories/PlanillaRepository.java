package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PlanillaRepository extends JpaRepository<Planilla, Long> {

    // LÓGICA DE BLOQUEO: Detecta si las fechas chocan con otra planilla ya guardada
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Planilla p WHERE p.fechaInicio <= :fin AND p.fechaFin >= :inicio")
    boolean existsOverlappingPlanilla(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // Cargar Planilla con todos sus detalles de trabajadores para el Historial
    @Query("SELECT p FROM Planilla p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.trabajador t LEFT JOIN FETCH t.puesto WHERE p.id = :id")
    Optional<Planilla> findByIdWithDetalles(@Param("id") Long id);
}