package dsi.plantilla.demo.dto;

import dsi.plantilla.demo.models.Trabajador;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanillaResumenDTO {
    private Trabajador trabajador;
    private Double horasBase;
    private Double horasExtrasDiurnas;
    private Double horasExtrasNocturnas;
    
    private Double salarioBase;
    private Double montoExtras;
    private Double bonos; // <-- NUEVO: Bonos ganados en el período
    private Double deducciones;
    private Double salarioNeto;
}