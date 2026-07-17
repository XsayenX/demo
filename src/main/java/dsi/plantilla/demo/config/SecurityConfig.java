package dsi.plantilla.demo.config;

import dsi.plantilla.demo.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas Públicas (Sin login)
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/webjars/**", "/uploads/**").permitAll()
                
                // 2. Dashboad y Rutas Básicas (Con login)
                .requestMatchers("/dashboard").authenticated()
                
                // 3. RUTAS RESTRINGIDAS POR ROLES (RBAC)
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/clientes/**").hasAnyRole("ADMINISTRADOR", "JEFE")
                .requestMatchers("/proyectos/**").hasAnyRole("ADMINISTRADOR", "JEFE")
                .requestMatchers("/cotizaciones/**").hasAnyRole("ADMINISTRADOR", "JEFE")
                
                .requestMatchers("/trabajadores/**").hasRole("SUPERVISOR")
                .requestMatchers("/asistencias/**").hasAnyRole("SUPERVISOR", "CONTADORA", "JEFE")
                
                .requestMatchers("/planillas/**").hasRole("CONTADORA")
                .requestMatchers("/facturacion/**").hasRole("CONTADORA")
                
                // RUTAS DE GASTOS Y FACTURAS (Para la Epic 7 que vimos antes)
                .requestMatchers("/facturas/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "CONTADORA", "JEFE")
                
                // INVENTARIO (Epic 8) <-- Añadir esto
                .requestMatchers("/inventario/**").hasAnyRole("ADMINISTRADOR", "JEFE", "SUPERVISOR")

                // 4. CUALQUIER OTRA RUTA: Obligar Login (ESTO SOLO SE PONE 1 VEZ AL FINAL)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
}