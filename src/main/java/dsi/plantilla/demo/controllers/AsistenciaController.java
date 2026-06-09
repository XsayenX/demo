package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.dto.AsistenciaDiariaDTO;
import dsi.plantilla.demo.models.*;
import dsi.plantilla.demo.repositories.*;
import dsi.plantilla.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired private AsistenciaRepository asistenciaRepository;
    @Autowired private TrabajadorRepository trabajadorRepository;
    @Autowired private TrabajadorService trabajadorService;
    @Autowired private AsistenciaService asistenciaService;
    @Autowired private HoraExtraRepository horaExtraRepository;

    @GetMapping
    public String listar(Model model) {
        List<Asistencia> todas = asistenciaRepository.findAll();
        TreeMap<LocalDate, List<Asistencia>> agrupadas = todas.stream()
                .collect(Collectors.groupingBy(Asistencia::getFecha, TreeMap::new, Collectors.toList()));
        
        long totalActivos = trabajadorRepository.findAll().stream().filter(Trabajador::isActivo).count();
        
        model.addAttribute("asistenciasPorDia", agrupadas.descendingMap());
        model.addAttribute("totalActivos", totalActivos);
        return "asistencias/lista";
    }

    @GetMapping("/nueva")
    public String elegirFecha() { return "asistencias/elegir-fecha"; }

    @GetMapping("/diaria")
    public String reporteDiario(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha, Model model) {
        List<Trabajador> activos = trabajadorRepository.findAll().stream()
                .filter(Trabajador::isActivo).collect(Collectors.toList());
        
        List<Asistencia> existentes = asistenciaRepository.findAll().stream()
                .filter(a -> a.getFecha().equals(fecha)).collect(Collectors.toList());

        List<Asistencia> listaFinal = new ArrayList<>();
        for (Trabajador t : activos) {
            Optional<Asistencia> asisOpt = existentes.stream()
                    .filter(a -> a.getTrabajador().getId().equals(t.getId())).findFirst();
            if (asisOpt.isPresent()) {
                listaFinal.add(asisOpt.get()); // Aquí el objeto ya lleva su ID de la base de datos
            } else {
                Asistencia nueva = new Asistencia();
                nueva.setTrabajador(t);
                nueva.setFecha(fecha);
                nueva.setEstado("PENDIENTE");
                listaFinal.add(nueva); // Sin ID
            }
        }
        model.addAttribute("fecha", fecha);
        model.addAttribute("dto", new AsistenciaDiariaDTO(listaFinal));
        return "asistencias/registrar-masivo";
    }

    @PostMapping("/guardar-masivo")
    public String guardarMasivo(@ModelAttribute AsistenciaDiariaDTO dto, RedirectAttributes flash) {
        // LÓGICA DE SEGURIDAD: Solo filtramos los que NO tienen ID (nuevos)
        // y que tengan las horas llenas. Los que tienen ID ya están en la DB, así que se ignoran.
        List<Asistencia> aGuardar = dto.getRegistros().stream()
                .filter(a -> a.getId() == null && a.getHoraEntrada() != null && a.getHoraSalida() != null)
                .collect(Collectors.toList());
        
        if(!aGuardar.isEmpty()) {
            asistenciaRepository.saveAll(aGuardar);
            flash.addFlashAttribute("success", "Se agregaron " + aGuardar.size() + " registros faltantes.");
        } else {
            flash.addFlashAttribute("info", "No se detectaron nuevos registros para guardar.");
        }
        
        return "redirect:/asistencias";
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
        model.addAttribute("trabajador", trabajadorOpt.get());
        model.addAttribute("jornadasPorMes", jornadasAgrupadas);
        model.addAttribute("totalBaseGlobal", String.format("%.2f", historial.stream().mapToDouble(Asistencia::getHorasBaseSiAprobado).sum()));
        model.addAttribute("totalDiurnaGlobal", String.format("%.2f", historial.stream().mapToDouble(Asistencia::getHorasDiurnasAprobadas).sum()));
        model.addAttribute("totalNocturnaGlobal", String.format("%.2f", historial.stream().mapToDouble(Asistencia::getHorasNocturnasAprobadas).sum()));
        return "asistencias/detalle";
    }

    @PostMapping("/revisar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'JEFE')")
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
        }
        return "redirect:/asistencias/detalle/" + (asistencia != null ? asistencia.getTrabajador().getId() : "");
    }

    @PostMapping("/horas-extra/guardar")
    public String guardarHoraExtra(@ModelAttribute HoraExtra horaExtra, RedirectAttributes flash) {
        Asistencia asis = asistenciaRepository.findById(horaExtra.getAsistencia().getId()).orElse(null);
        if (asis != null && "PENDIENTE".equals(asis.getEstado())) {
            horaExtraRepository.save(horaExtra);
            flash.addFlashAttribute("success", "Horas extra añadidas.");
        }
        return "redirect:/asistencias/detalle/" + (asis != null ? asis.getTrabajador().getId() : "");
    }
}