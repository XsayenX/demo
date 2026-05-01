package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Trabajador;
import dsi.plantilla.demo.services.TrabajadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    @Autowired
    private TrabajadorService trabajadorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("trabajadores", trabajadorService.listarTodos());
        return "trabajadores/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("trabajador", new Trabajador());
        return "trabajadores/crear";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Trabajador> trabajador = trabajadorService.buscarPorId(id);
        if (trabajador.isEmpty()) {
            flash.addFlashAttribute("error", "El trabajador solicitado no existe.");
            return "redirect:/trabajadores";
        }
        model.addAttribute("trabajador", trabajador.get());
        return "trabajadores/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Trabajador trabajador, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return trabajador.getId() == null ? "trabajadores/crear" : "trabajadores/editar";
        }

        // Validación de duplicados solo para registros nuevos
        if (trabajador.getId() == null) {
            if (trabajadorService.existeDui(trabajador.getDui())) {
                flash.addFlashAttribute("error", "Error: El DUI ingresado ya pertenece a otro trabajador.");
                return "redirect:/trabajadores/nuevo";
            }
            if (trabajadorService.existeNss(trabajador.getNss())) {
                flash.addFlashAttribute("error", "Error: El NSS ingresado ya está registrado.");
                return "redirect:/trabajadores/nuevo";
            }
        }

        trabajadorService.guardar(trabajador);
        flash.addFlashAttribute("success", "Datos del trabajador guardados con éxito.");
        return "redirect:/trabajadores";
    }
}