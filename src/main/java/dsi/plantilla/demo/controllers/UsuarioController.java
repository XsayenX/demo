package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Usuario;
import dsi.plantilla.demo.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/crear";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "usuarios/crear";
        }
        
        if (usuarioService.existeUsername(usuario.getUsername())) {
            flash.addFlashAttribute("error", "El nombre de usuario ya existe");
            return "redirect:/usuarios/nuevo";
        }

        usuarioService.guardar(usuario);
        flash.addFlashAttribute("success", "Usuario creado con éxito");
        return "redirect:/usuarios";
    }
}