package dsi.plantilla.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "asistencias")
@Data
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe seleccionar un trabajador")
    @ManyToOne(fetch = FetchType.LAZY)
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

    // Método para calcular horas antes de persistir
    @PrePersist
    @PreUpdate
    public void calcularHoras() {
        if (horaEntrada != null && horaSalida != null) {
            long minutos = java.time.Duration.between(horaEntrada, horaSalida).toMinutes();
            this.horasTotales = minutos / 60.0;
        }
    }
}