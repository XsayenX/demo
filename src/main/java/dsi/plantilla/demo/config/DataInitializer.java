package dsi.plantilla.demo.config;

import dsi.plantilla.demo.models.Rol;
import dsi.plantilla.demo.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> nombresRoles = Arrays.asList("ADMINISTRADOR", "JEFE", "SUPERVISOR", "CONTADORA");

        for (String nombre : nombresRoles) {
            if (rolRepository.findByNombre(nombre).isEmpty()) {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombre(nombre);
                rolRepository.save(nuevoRol);
            }
        }
    }
}