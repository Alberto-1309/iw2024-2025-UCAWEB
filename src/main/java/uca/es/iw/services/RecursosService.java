package uca.es.iw.services;

import org.springframework.stereotype.Service;
import uca.es.iw.data.*;

import java.util.List;
import java.util.Optional;

@Service
public class RecursosService {
    private final RecursosRepository recursosRepository;
    private final ProyectoRepository proyectoRepository;

    public RecursosService(RecursosRepository recursosRepository, ProyectoRepository proyectoRepository) {
        this.recursosRepository = recursosRepository;
        this.proyectoRepository = proyectoRepository;
    }

    public Double getPresupuestoTotal() {
        return recursosRepository.findPresupuestoTotal();
    }

    public Integer getRecursosHumanosTotal() {
        return recursosRepository.findRecursosHumanosTotal();
    }

    public Double getPresupuestoRestante() {
        return recursosRepository.findPresupuestoRestante();
    }

    public Integer getRecursosHumanosRestantes() {
        return recursosRepository.findRecursosHumanosRestantes();
    }

    public Recursos findById(long id) {
        return recursosRepository.findById(id).orElseGet(Recursos::new);
    }

    public void updateRecursosRestantes(double presupuestoRestante, int recursosRestantes) {
        Recursos recursos = findById(1);
        recursos.setPresupuestoRestante(presupuestoRestante);
        recursos.setRecursosHumanosRestantes(recursosRestantes);
        recursosRepository.save(recursos);
    }

    public Recursos getRecursosByConvocatoriaId(long id) {
        return recursosRepository.findByIdConvocatoria(id).orElse(new Recursos());
    }

    public void saveRecursos(Recursos recursos) {
        recursosRepository.save(recursos);
    }
}
