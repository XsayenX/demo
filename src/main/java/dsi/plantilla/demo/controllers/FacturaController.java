package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Factura;
import dsi.plantilla.demo.models.Usuario;
import dsi.plantilla.demo.services.FacturaService;
import dsi.plantilla.demo.services.FileService;
import dsi.plantilla.demo.services.ProyectoService;
import dsi.plantilla.demo.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/facturas")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'CONTADORA', 'JEFE')")
public class FacturaController {

    @Autowired private FacturaService facturaService;
    @Autowired private ProyectoService proyectoService;
    @Autowired private FileService fileService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("facturas", facturaService.buscar(q));
        model.addAttribute("proyectos", proyectoService.buscar(null));
        model.addAttribute("q", q);
        model.addAttribute("nuevaFactura", new Factura());
        return "facturas/lista";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('SUPERVISOR')") // Solo el supervisor sube fotos desde campo
    public String guardar(@Valid @ModelAttribute("nuevaFactura") Factura factura, BindingResult result,
                          @RequestParam(value = "fileImagen", required = false) MultipartFile fileImagen,
                          Authentication auth, RedirectAttributes flash) {
        
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Error: Verifique los datos de la factura.");
            return "redirect:/facturas";
        }

        // HU-20 Tarea 2: Guardar archivo Multipart
        if (fileImagen != null && !fileImagen.isEmpty()) {
            factura.setArchivoImagen(fileService.guardarArchivo(fileImagen));
        } else {
            flash.addFlashAttribute("error", "La fotografía del comprobante es obligatoria.");
            return "redirect:/facturas";
        }

        // Identificamos quién subió la factura
        Optional<Usuario> supervisor = usuarioService.buscarPorUsername(auth.getName());
        supervisor.ifPresent(factura::setSupervisor);

        facturaService.guardar(factura);
        flash.addFlashAttribute("success", "Gasto registrado correctamente.");
        return "redirect:/facturas";
    }
}