package dsi.plantilla.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "asistencias")
@Data
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe seleccionar un trabajador")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trabajador_id")
    private Trabajador trabajador;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotNull(message = "La hora de entrada es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaEntrada;

    @NotNull(message = "La hora de salida es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaSalida;

    private Double horasTotales;

    // Relación con Horas Extra - EAGER para evitar errores en la sumatoria de la vista
    @OneToMany(mappedBy = "asistencia", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<HoraExtra> horasExtras;

    @PrePersist
    @PreUpdate
    public void calcularHoras() {
        if (horaEntrada != null && horaSalida != null) {
            long minutos = java.time.Duration.between(horaEntrada, horaSalida).toMinutes();
            this.horasTotales = Math.max(0, minutos / 60.0);
        }
    }
    
    // Método para sumar las extras de este día específico
    public Double getTotalHorasExtra() {
        if (horasExtras == null || horasExtras.isEmpty()) return 0.0;
        return horasExtras.stream().mapToDouble(HoraExtra::getCantidad).sum();
    }
}