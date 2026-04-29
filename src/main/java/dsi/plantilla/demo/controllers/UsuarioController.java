package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Usuario;
import dsi.plantilla.demo.models.Rol;
import dsi.plantilla.demo.services.UsuarioService;
import dsi.plantilla.demo.services.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @GetMapping({"/", ""})
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("allRoles", rolService.listarTodos());
        return "usuarios/crear";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isEmpty()) {
            flash.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario.get());
        model.addAttribute("allRoles", rolService.listarTodos());
        return "usuarios/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", rolService.listarTodos());
            return usuario.getId() == null ? "usuarios/crear" : "usuarios/editar";
        }

        // Validación lógica para nuevo usuario
        if (usuario.getId() == null && usuarioService.existeUsername(usuario.getUsername())) {
            flash.addFlashAttribute("error", "El nombre de usuario ya existe");
            model.addAttribute("allRoles", rolService.listarTodos());
            return "redirect:/usuarios/nuevo";
        }

        usuarioService.guardar(usuario);
        flash.addFlashAttribute("success", "Usuario procesado con éxito");
        return "redirect:/usuarios";
    }
}