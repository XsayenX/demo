package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.models.HoraExtra;
import dsi.plantilla.demo.models.Trabajador;
import dsi.plantilla.demo.services.AsistenciaService;
import dsi.plantilla.demo.services.TrabajadorService;
import dsi.plantilla.demo.repositories.TrabajadorRepository;
import dsi.plantilla.demo.repositories.HoraExtraRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private TrabajadorService trabajadorService;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private HoraExtraRepository horaExtraRepository;

    @GetMapping
    public String listar(Model model) {
        List<Trabajador> trabajadores = trabajadorRepository.findTrabajadoresConAsistencia();
        model.addAttribute("trabajadores", trabajadores);
        return "asistencias/lista";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Trabajador> trabajadorOpt = trabajadorService.buscarPorId(id);
        if (trabajadorOpt.isEmpty()) {
            flash.addFlashAttribute("error", "Trabajador no encontrado.");
            return "redirect:/asistencias";
        }

        List<Asistencia> historial = asistenciaService.historialPorTrabajador(id);
        
        // Agrupar jornadas por Mes y Año
        Map<String, List<Asistencia>> jornadasAgrupadas = historial.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getFecha().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase() 
                             + " " + a.getFecha().getYear(),
                        LinkedHashMap::new, 
                        Collectors.toList()
                ));

        // Calcular total global (Horas Base)
        Double totalHorasGlobal = historial.stream()
                .mapToDouble(a -> a.getHorasTotales() != null ? a.getHorasTotales() : 0.0)
                .sum();

        model.addAttribute("trabajador", trabajadorOpt.get());
        model.addAttribute("jornadasPorMes", jornadasAgrupadas);
        model.addAttribute("totalHorasGlobal", String.format("%.2f", totalHorasGlobal));
        
        // Objeto vacío para el modal de horas extra
        model.addAttribute("nuevaHoraExtra", new HoraExtra());
        
        return "asistencias/detalle";
    }

    @PostMapping("/horas-extra/guardar")
    public String guardarHoraExtra(@ModelAttribute HoraExtra horaExtra, RedirectAttributes flash) {
        if (horaExtra.getAsistencia() == null || horaExtra.getCantidad() == null || horaExtra.getCantidad() <= 0) {
            flash.addFlashAttribute("error", "Datos de horas extra inválidos.");
            return "redirect:/asistencias";
        }
        
        horaExtraRepository.save(horaExtra);
        flash.addFlashAttribute("success", "Horas extra registradas correctamente.");
        
        // Recuperar trabajador para volver al detalle
        Asistencia asis = asistenciaService.listarTodas().stream()
                .filter(a -> a.getId().equals(horaExtra.getAsistencia().getId()))
                .findFirst().orElse(null);
                
        return "redirect:/asistencias/detalle/" + asis.getTrabajador().getId();
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("asistencia", new Asistencia());
        model.addAttribute("trabajadores", trabajadorService.listarTodos());
        return "asistencias/registrar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("asistencia") Asistencia asistencia, 
                          BindingResult result, 
                          Model model, 
                          RedirectAttributes flash) {
        
        if (result.hasErrors()) {
            model.addAttribute("trabajadores", trabajadorService.listarTodos());
            return "asistencias/registrar";
        }

        if (asistenciaService.yaRegistroAsistencia(asistencia)) {
            model.addAttribute("trabajadores", trabajadorService.listarTodos());
            model.addAttribute("error", "Error: Ya existe un registro para este trabajador en la fecha seleccionada.");
            return "asistencias/registrar";
        }

        asistenciaService.guardar(asistencia);
        flash.addFlashAttribute("success", "Jornada registrada con éxito.");
        return "redirect:/asistencias/detalle/" + asistencia.getTrabajador().getId();
    }
}