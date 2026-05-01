package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.services.AsistenciaService;
import dsi.plantilla.demo.services.TrabajadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private TrabajadorService trabajadorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asistencias", asistenciaService.listarTodas());
        return "asistencias/lista";
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("asistencia", new Asistencia());
        model.addAttribute("trabajadores", trabajadorService.listarTodos());
        return "asistencias/registrar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Asistencia asistencia, BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("trabajadores", trabajadorService.listarTodos());
            return "asistencias/registrar";
        }

        // Tarea 3: Evitar duplicados
        if (asistenciaService.yaRegistroAsistencia(asistencia)) {
            flash.addFlashAttribute("error", "Error: Este trabajador ya tiene una jornada registrada para el día seleccionado.");
            return "redirect:/asistencias/registrar";
        }

        asistenciaService.guardar(asistencia);
        flash.addFlashAttribute("success", "Jornada registrada correctamente.");
        return "redirect:/asistencias";
    }
}