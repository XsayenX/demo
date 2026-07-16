package dsi.plantilla.demo.services;

import dsi.plantilla.demo.models.Factura;
import dsi.plantilla.demo.repositories.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturaService {

    @Autowired private FacturaRepository facturaRepository;

    @Transactional(readOnly = true)
    public List<Factura> buscar(String termino) {
        if (termino != null && !termino.trim().isEmpty()) {
            return facturaRepository.buscarPorProveedorOProyecto(termino);
        }
        return facturaRepository.findAll();
    }

    @Transactional
    public Factura guardar(Factura factura) {
        return facturaRepository.save(factura);
    }

    @Transactional(readOnly = true)
    public Double calcularGastoAcumulado(Long proyectoId) {
        return facturaRepository.sumarGastosPorProyecto(proyectoId);
    }
}