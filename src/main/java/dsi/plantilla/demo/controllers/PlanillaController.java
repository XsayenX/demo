package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.dto.PlanillaResumenDTO;
import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.models.Trabajador;
import dsi.plantilla.demo.repositories.AsistenciaRepository;
import dsi.plantilla.demo.repositories.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/planillas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADORA')")
public class PlanillaController {

    @Autowired private AsistenciaRepository asistenciaRepository;
    @Autowired private TrabajadorRepository trabajadorRepository;

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

        List<Asistencia> aprobadas = asistenciaRepository.findAll().stream()
                .filter(a -> "APROBADO".equals(a.getEstado()))
                .filter(a -> !a.getFecha().isBefore(inicio) && !a.getFecha().isAfter(fin))
                .collect(Collectors.toList());

        Map<Trabajador, List<Asistencia>> porTrabajador = aprobadas.stream()
                .collect(Collectors.groupingBy(Asistencia::getTrabajador));

        List<PlanillaResumenDTO> resumen = new ArrayList<>();
        porTrabajador.forEach((t, lista) -> {
            Double base = lista.stream().mapToDouble(Asistencia::getHorasTotales).sum();
            Double diurnas = lista.stream().mapToDouble(Asistencia::getHorasDiurnasTotales).sum();
            Double nocturnas = lista.stream().mapToDouble(Asistencia::getHorasNocturnasTotales).sum();
            resumen.add(new PlanillaResumenDTO(t, base, diurnas, nocturnas));
        });

        model.addAttribute("resumen", resumen);
        model.addAttribute("fInicio", inicio);
        model.addAttribute("fFin", fin);
        return "planillas/resultado";
    }
}