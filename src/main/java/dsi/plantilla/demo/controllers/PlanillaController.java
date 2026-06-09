package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.dto.PlanillaResumenDTO;
import dsi.plantilla.demo.services.PlanillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/planillas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADORA')")
public class PlanillaController {

    @Autowired 
    private PlanillaService planillaService;

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("fechaInicio", LocalDate.now().withDayOfMonth(1));
        model.addAttribute("fechaFin", LocalDate.now());
        return "planillas/generar";
    }

    @PostMapping("/generar")
    public String generarPlanilla(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin,
            Model model) {

        // Solo pre-visualizamos
        List<PlanillaResumenDTO> resumen = planillaService.simularPlanilla(inicio, fin);

        model.addAttribute("resumen", resumen);
        model.addAttribute("fInicio", inicio);
        model.addAttribute("fFin", fin);
        
        // Sumatorias globales financieras
        model.addAttribute("totalNomina", resumen.stream().mapToDouble(PlanillaResumenDTO::getSalarioNeto).sum());
        model.addAttribute("totalDeducciones", resumen.stream().mapToDouble(PlanillaResumenDTO::getDeducciones).sum());
        
        return "planillas/resultado";
    }

    // NUEVO ENDPOINT: Guarda los cálculos en Base de Datos (Cierre Masivo)
    @PostMapping("/guardar")
    public String guardarPlanilla(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fin,
            RedirectAttributes flash) {
        
        planillaService.guardarPlanilla(inicio, fin);
        flash.addFlashAttribute("success", "Planilla registrada y cerrada exitosamente en la Base de Datos.");
        return "redirect:/planillas"; // Luego lo redirigiremos al historial de la HU-11
    }
}