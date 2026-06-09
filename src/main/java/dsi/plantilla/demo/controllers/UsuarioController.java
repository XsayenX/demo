package dsi.plantilla.demo.controllers;

import dsi.plantilla.demo.models.Usuario;
import dsi.plantilla.demo.services.UsuarioService;
import dsi.plantilla.demo.services.RolService;
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

    @Autowired
    private RolService rolService;

    // ACTUALIZADO: Recibe el parámetro "q"
    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("usuarios", usuarioService.buscar(q));
        model.addAttribute("q", q);
        model.addAttribute("nuevoUsuario", new Usuario());
        model.addAttribute("allRoles", rolService.listarTodos());
        return "usuarios/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/usuarios";
        }
        if (usuario.getId() == null && (usuario.getPassword() == null || usuario.getPassword().trim().length() < 6)) {
            flash.addFlashAttribute("error", "Para un nuevo usuario, la contraseña es obligatoria (mín. 6 caracteres).");
            return "redirect:/usuarios";
        }
        if (usuario.getId() == null) {
            if (usuarioService.existeUsername(usuario.getUsername())) {
                flash.addFlashAttribute("error", "Error: El nombre de usuario ya está en uso.");
                return "redirect:/usuarios";
            }
            if (usuarioService.existeEmail(usuario.getEmail())) {
                flash.addFlashAttribute("error", "Error: El correo electrónico ya está registrado.");
                return "redirect:/usuarios";
            }
        }
        usuarioService.guardar(usuario);
        flash.addFlashAttribute("success", "Usuario guardado correctamente.");
        return "redirect:/usuarios";
    }
}