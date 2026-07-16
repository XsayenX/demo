package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    
    // Generar el correlativo (Ej: COT-2026-001)
    @Query("SELECT COUNT(c) FROM Cotizacion c")
    long countCotizaciones();

    @Query("SELECT c FROM Cotizacion c WHERE LOWER(c.numero) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(c.cliente.nombre) LIKE LOWER(CONCAT('%', :term, '%')) ORDER BY c.id DESC")
    List<Cotizacion> buscarPorNumeroOCliente(@Param("term") String term);
}