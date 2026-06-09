package dsi.plantilla.demo.dto;

import dsi.plantilla.demo.models.Trabajador;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanillaResumenDTO {
    private Trabajador trabajador;
    private Double totalHorasBase;
    private Double totalExtrasDiurnas;
    private Double totalExtrasNocturnas;
}