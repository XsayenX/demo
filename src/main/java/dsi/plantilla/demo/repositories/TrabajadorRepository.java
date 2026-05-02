package dsi.plantilla.demo.repositories;

import dsi.plantilla.demo.models.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    boolean existsByDui(String dui);
    boolean existsByNss(String nss);

    @Query("SELECT t FROM Trabajador t WHERE " +
           "LOWER(t.nombre) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "t.dui LIKE CONCAT('%', :term, '%')")
    List<Trabajador> buscarPorNombreODui(@Param("term") String term);

    // NUEVA CONSULTA: Obtiene trabajadores que tienen registros en la tabla asistencias
    @Query("SELECT DISTINCT t FROM Trabajador t JOIN Asistencia a ON a.trabajador.id = t.id")
    List<Trabajador> findTrabajadoresConAsistencia();
}