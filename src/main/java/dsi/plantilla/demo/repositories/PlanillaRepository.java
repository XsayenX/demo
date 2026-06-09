package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanillaRepository extends JpaRepository<Planilla, Long> {
}