package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Proyecto;
import dsi.plantilla.demo.services.ClienteService;
import dsi.plantilla.demo.services.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proyectos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'JEFE')")
public class ProyectoController {

    @Autowired private ProyectoService proyectoService;
    @Autowired private ClienteService clienteService; // Para llenar el <select> de clientes

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("proyectos", proyectoService.buscar(q));
        model.addAttribute("clientes", clienteService.buscar(null));
        model.addAttribute("q", q);
        model.addAttribute("nuevoProyecto", new Proyecto());
        return "proyectos/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("nuevoProyecto") Proyecto proyecto, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Error en el formulario: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/proyectos";
        }

        // Tarea 3 de HU-13: Validar reglas de negocio en fechas
        if (proyecto.getFechaFin() != null && proyecto.getFechaFin().isBefore(proyecto.getFechaInicio())) {
            flash.addFlashAttribute("error", "Error: La fecha de finalización no puede ser anterior a la fecha de inicio.");
            return "redirect:/proyectos";
        }

        proyectoService.guardar(proyecto);
        flash.addFlashAttribute("success", "Proyecto guardado exitosamente.");
        return "redirect:/proyectos";
    }
}