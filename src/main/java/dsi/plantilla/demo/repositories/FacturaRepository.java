package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    @Query("SELECT f FROM Factura f WHERE LOWER(f.proveedor) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(f.proyecto.nombre) LIKE LOWER(CONCAT('%', :term, '%')) ORDER BY f.fecha DESC")
    List<Factura> buscarPorProveedorOProyecto(@Param("term") String term);

    @Query("SELECT COALESCE(SUM(f.monto), 0) FROM Factura f WHERE f.proyecto.id = :proyectoId")
    Double sumarGastosPorProyecto(@Param("proyectoId") Long proyectoId);

    // NUEVO: Facturas específicas de un proyecto
    List<Factura> findByProyectoIdOrderByFechaDesc(Long proyectoId);
}