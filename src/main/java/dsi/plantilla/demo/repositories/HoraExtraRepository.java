package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.HoraExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HoraExtraRepository extends JpaRepository<HoraExtra, Long> {
    List<HoraExtra> findByAsistenciaId(Long asistenciaId);
}