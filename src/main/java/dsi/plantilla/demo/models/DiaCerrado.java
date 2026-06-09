package dsi.plantilla.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "dias_cerrados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaCerrado {
    
    @Id
    private LocalDate fecha;

}