package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Trabajador;
import dsi.plantilla.demo.models.Puesto;
import dsi.plantilla.demo.services.TrabajadorService;
import dsi.plantilla.demo.repositories.PuestoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trabajadores")
@PreAuthorize("hasRole('SUPERVISOR')") // SOLO EL SUPERVISOR ENTRA AQUÍ
public class TrabajadorController {

    @Autowired private TrabajadorService trabajadorService;
    @Autowired private PuestoRepository puestoRepository;

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("trabajadores", trabajadorService.buscar(q));
        model.addAttribute("q", q);
        model.addAttribute("puestos", puestoRepository.findAll());
        model.addAttribute("nuevoTrabajador", new Trabajador());
        return "trabajadores/lista";
    }

    @GetMapping("/puestos")
    public String listarPuestos(Model model) {
        model.addAttribute("puestos", puestoRepository.findAll());
        model.addAttribute("nuevoPuesto", new Puesto());
        return "trabajadores/puestos";
    }

    @PostMapping("/puestos/guardar")
    public String guardarPuesto(@ModelAttribute Puesto puesto, RedirectAttributes flash) {
        puestoRepository.save(puesto);
        flash.addFlashAttribute("success", "Puesto de trabajo registrado.");
        return "redirect:/trabajadores/puestos";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Trabajador trabajador, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Error en el formulario: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/trabajadores";
        }
        if (trabajador.getId() == null && trabajadorService.existeDui(trabajador.getDui())) {
            flash.addFlashAttribute("error", "El DUI ingresado ya está registrado.");
            return "redirect:/trabajadores";
        }
        trabajadorService.guardar(trabajador);
        flash.addFlashAttribute("success", "Datos del trabajador guardados correctamente.");
        return "redirect:/trabajadores";
    }
}