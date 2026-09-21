package dsi.plantilla.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Página de presentación pública (Antigravedad)
    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    // Panel de control (Privado)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "home";
    }
}