package dsi.plantilla.demo.dto;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.models.Trabajador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDiariaDTO {
    private List<Asistencia> registros;
}