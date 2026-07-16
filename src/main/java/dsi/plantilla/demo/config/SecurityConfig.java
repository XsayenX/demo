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
                // AÑADIMOS "/uploads/**" PARA PERMITIR VER LAS FOTOS Y PDFs
                .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/webjars/**", "/uploads/**").permitAll()
                .requestMatchers("/dashboard").authenticated()
                
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/clientes/**").hasAnyRole("ADMINISTRADOR", "JEFE")
                
                .requestMatchers("/trabajadores/**").hasRole("SUPERVISOR")
                .requestMatchers("/asistencias/**").hasAnyRole("SUPERVISOR", "CONTADORA", "JEFE")
                .requestMatchers("/planillas/**").hasRole("CONTADORA")
                .requestMatchers("/facturacion/**").hasRole("CONTADORA") // <-- NUEVA LÍNEA
                

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