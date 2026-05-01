package dsi.plantilla.demo.services;

import dsi.plantilla.demo.models.Asistencia;
import dsi.plantilla.demo.repositories.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Transactional(readOnly = true)
    public List<Asistencia> listarTodas() {
        return asistenciaRepository.findAll();
    }

    @Transactional
    public Asistencia guardar(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    public boolean yaRegistroAsistencia(Asistencia asistencia) {
        return asistenciaRepository.existsByTrabajadorAndFecha(asistencia.getTrabajador(), asistencia.getFecha());
    }
}