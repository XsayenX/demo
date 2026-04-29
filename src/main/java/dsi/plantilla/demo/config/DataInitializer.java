package dsi.plantilla.demo.config;

import dsi.plantilla.demo.models.Rol;
import dsi.plantilla.demo.models.Usuario;
import dsi.plantilla.demo.repositories.RolRepository;
import dsi.plantilla.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el PasswordEncoder

    @Override
    public void run(String... args) throws Exception {
        // 1. Sembrar Roles
        List<String> nombresRoles = Arrays.asList("ADMINISTRADOR", "JEFE", "SUPERVISOR", "CONTADORA");

        for (String nombre : nombresRoles) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(nombre);
                rolRepository.save(nuevoRol);
            }
        }

        // 2. Sembrar un usuario ADMINISTRADOR por defecto (para poder iniciar sesión)
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario adminUser = new Usuario();
            adminUser.setUsername("admin");
            adminUser.setNombreCompleto("Administrador del Sistema");
            adminUser.setEmail("admin@constructora.com");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Contraseña encriptada
            adminUser.setActivo(true);

            // Asignar el rol ADMINISTRADOR
            Rol adminRol = rolRepository.findByNombre("ADMINISTRADOR")
                                        .orElseThrow(() -> new RuntimeException("Error: Rol ADMINISTRADOR no encontrado."));
            Set<Rol> roles = new HashSet<>();
            roles.add(adminRol);
            adminUser.setRoles(roles);

            usuarioRepository.save(adminUser);
        }
    }
}