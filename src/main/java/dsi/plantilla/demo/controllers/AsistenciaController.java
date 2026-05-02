package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.models.HoraExtra;
import dsi.plantilla.demo.models.Trabajador;
import dsi.plantilla.demo.services.AsistenciaService;
import dsi.plantilla.demo.services.TrabajadorService;
import dsi.plantilla.demo.repositories.TrabajadorRepository;
import dsi.plantilla.demo.repositories.HoraExtraRepository;
import dsi.plantilla.demo.repositories.AsistenciaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("trabajadores", trabajadorRepository.findTrabajadoresConAsistencia());
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
        
        Map<String, List<Asistencia>> jornadasAgrupadas = historial.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getFecha().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase() 
                             + " " + a.getFecha().getYear(),
                        LinkedHashMap::new, Collectors.toList()
                ));

        Double totalBase = historial.stream().mapToDouble(Asistencia::getHorasBaseSiAprobado).sum();
        Double totalDiurna = historial.stream().mapToDouble(Asistencia::getHorasDiurnasAprobadas).sum();
        Double totalNocturna = historial.stream().mapToDouble(Asistencia::getHorasNocturnasAprobadas).sum();

        model.addAttribute("trabajador", trabajadorOpt.get());
        model.addAttribute("jornadasPorMes", jornadasAgrupadas);
        model.addAttribute("totalBaseGlobal", String.format("%.2f", totalBase));
        model.addAttribute("totalDiurnaGlobal", String.format("%.2f", totalDiurna));
        model.addAttribute("totalNocturnaGlobal", String.format("%.2f", totalNocturna));
        
        return "asistencias/detalle";
    }

    @PostMapping("/revisar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'JEFE')") // Admin puede aprobar
    public String revisarAsistencia(@RequestParam Long id, @RequestParam String accion, 
                                   @RequestParam(required = false) String observaciones,
                                   Authentication auth, RedirectAttributes flash) {
        Asistencia asistencia = asistenciaRepository.findById(id).orElse(null);
        if (asistencia != null) {
            asistencia.setEstado(accion.equals("APROBAR") ? "APROBADO" : "RECHAZADO");
            asistencia.setObservacionesJefe(observaciones);
            asistencia.setFechaRevision(LocalDateTime.now());
            asistencia.setRevisadoPor(auth.getName());
            asistenciaRepository.save(asistencia);
            flash.addFlashAttribute("success", "Estado actualizado correctamente por " + auth.getName());
        }
        return "redirect:/asistencias/detalle/" + (asistencia != null ? asistencia.getTrabajador().getId() : "");
    }

    @PostMapping("/horas-extra/guardar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR')") // Admin puede registrar extras
    public String guardarHoraExtra(@ModelAttribute HoraExtra horaExtra, RedirectAttributes flash) {
        Asistencia asis = asistenciaRepository.findById(horaExtra.getAsistencia().getId()).orElse(null);
        
        // El admin puede saltarse el bloqueo de PENDIENTE si lo desea, 
        // pero por regla de integridad lo mantenemos solo para Pendientes para todos.
        if (asis != null && "PENDIENTE".equals(asis.getEstado())) {
            horaExtraRepository.save(horaExtra);
            flash.addFlashAttribute("success", "Horas extra registradas.");
        } else {
            flash.addFlashAttribute("error", "No se puede modificar un registro que ya no está pendiente.");
        }
        return "redirect:/asistencias/detalle/" + (asis != null ? asis.getTrabajador().getId() : "");
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("asistencia", new Asistencia());
        model.addAttribute("trabajadores", trabajadorService.listarTodos());
        return "asistencias/registrar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("asistencia") Asistencia asistencia, BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("trabajadores", trabajadorService.listarTodos());
            return "asistencias/registrar";
        }
        asistenciaService.guardar(asistencia);
        return "redirect:/asistencias/detalle/" + asistencia.getTrabajador().getId();
    }
}