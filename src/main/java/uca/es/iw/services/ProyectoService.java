package uca.es.iw.services;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uca.es.iw.data.*;
import uca.es.iw.security.AuthenticatedUser;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    private final UserRepository userRepository;

    private final PonderacionesRepository ponderacionesRepository;

    private final AuthenticatedUser authenticatedUser;

    private final ConvocatoriaRepository convocatoriaRepository; // Agregado

    private Long currentUserId;

    public ProyectoService(ProyectoRepository proyectoRepository, UserRepository userRepository,
                           PonderacionesRepository ponderacionesRepository,
                           ConvocatoriaRepository convocatoriaRepository,  // Agregado
                           AuthenticatedUser authenticatedUser) {
        this.proyectoRepository = proyectoRepository;
        this.userRepository = userRepository;
        this.ponderacionesRepository = ponderacionesRepository;
        this.authenticatedUser = authenticatedUser;
        this.convocatoriaRepository = convocatoriaRepository; // Initialize convocatoriaRepository
    }

    public Proyecto guardarProyecto(String titulo, String nombrecorto, byte[] memoria, String nombresolicitante, String correo, String unidad, String select, int importancia, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, List<String> selectedValues, byte[] especificaciones, byte[] presupuesto, Convocatoria convocatoria) {
        validateProjectData(titulo, nombrecorto, memoria, nombresolicitante, correo, unidad, select, importancia, interesados, financiacion, alcance, fechaObjetivo, normativa, selectedValues);
        String estado = "PENDIENTE";

        boolean AOE1 = selectedValues.contains("Innovar, rediseñar y atualizar nuestra oferta formativa para adaptarla a las necesidades sociales y económicas de nuestro entorno.");
        boolean AOE2 = selectedValues.contains("Conseguir los niveles más altos de calidad en nuestra oferta formativa propia y reglada.");
        boolean AOE3 = selectedValues.contains("Aumentar significativamente nuestro posicidonamiento en investigación y transferir de forma relevante y útil nuestra investigación a nuestro tejido social y productivo.");
        boolean AOE4 = selectedValues.contains("Consolidar un modelo de gobierno sostenible y socialmente responsable.");
        boolean AOE5 = selectedValues.contains("Conseguir que la transparencia sea un valor distintivo y relevante en la UCA.");
        boolean AOE6 = selectedValues.contains("Generar valor compartido con la Comunidad Universitaria.");
        boolean AOE7 = selectedValues.contains("Reforzar la importancia del papel de la UCA en la sociedad.");

        authenticatedUser.get().ifPresent(authenticatedUser -> {
            currentUserId = authenticatedUser.getId();
        });

        Proyecto proyecto = new Proyecto();
        proyecto.setTitulo(titulo);
        proyecto.setNombreCorto(nombrecorto);
        proyecto.setMemoria(memoria);
        proyecto.setNombreSolicitante(nombresolicitante);
        proyecto.setCorreoSolicitante(correo);
        proyecto.setUnidadSolicitante(unidad);
        proyecto.setPromotor(select);
        proyecto.setImportancia(importancia);
        proyecto.setInteresados(interesados);
        proyecto.setFinanciacion(financiacion);
        proyecto.setAlcance(alcance);
        proyecto.setFechaObjetivo(fechaObjetivo);
        proyecto.setNormativa(normativa);
        proyecto.setEstado(estado);
        proyecto.setCreadoId(currentUserId);
        proyecto.setCalificado(false);
        proyecto.setArchivado(false);
        proyecto.setConvocatoria(convocatoria);
        proyecto.setAoe1(AOE1);
        proyecto.setAoe2(AOE2);
        proyecto.setAoe3(AOE3);
        proyecto.setAoe4(AOE4);
        proyecto.setAoe5(AOE5);
        proyecto.setAoe6(AOE6);
        proyecto.setAoe7(AOE7);

        proyecto.setFechaCreado(LocalDate.now());

        proyecto.setEspecificaciones(especificaciones);
        proyecto.setPresupuestos(presupuesto);
        return proyectoRepository.save(proyecto);
    }

    public Proyecto updateProject (Long id, String titulo, String nombrecorto, byte[] memoria, String nombresolicitante, String correo, String unidad, String select, int importancia, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, List<String> selectedValues, byte[] especificaciones, byte[] presupuesto){
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con id: " + id));

        validateUpdateData(titulo, nombrecorto, memoria, nombresolicitante, correo, unidad, select, importancia, interesados, financiacion, alcance, fechaObjetivo, normativa, selectedValues);

        boolean AOE1 = selectedValues.contains("Innovar, rediseñar y atualizar nuestra oferta formativa para adaptarla a las necesidades sociales y económicas de nuestro entorno.");
        boolean AOE2 = selectedValues.contains("Conseguir los niveles más altos de calidad en nuestra oferta formativa propia y reglada.");
        boolean AOE3 = selectedValues.contains("Aumentar significativamente nuestro posicidonamiento en investigación y transferir de forma relevante y útil nuestra investigación a nuestro tejido social y productivo.");
        boolean AOE4 = selectedValues.contains("Consolidar un modelo de gobierno sostenible y socialmente responsable.");
        boolean AOE5 = selectedValues.contains("Conseguir que la transparencia sea un valor distintivo y relevante en la UCA.");
        boolean AOE6 = selectedValues.contains("Generar valor compartido con la Comunidad Universitaria.");
        boolean AOE7 = selectedValues.contains("Reforzar la importancia del papel de la UCA en la sociedad.");

        authenticatedUser.get().ifPresent(authenticatedUser -> {
            currentUserId = authenticatedUser.getId();
        });

        proyecto.setTitulo(titulo);
        proyecto.setNombreCorto(nombrecorto);
        proyecto.setNombreSolicitante(nombresolicitante);
        proyecto.setCorreoSolicitante(correo);
        proyecto.setUnidadSolicitante(unidad);
        proyecto.setPromotor(select);
        proyecto.setImportancia(importancia);
        proyecto.setInteresados(interesados);
        proyecto.setFinanciacion(financiacion);
        proyecto.setAlcance(alcance);
        proyecto.setFechaObjetivo(fechaObjetivo);
        proyecto.setNormativa(normativa);
        proyecto.setCalificado(false);
        proyecto.setArchivado(false);

        proyecto.setAoe1(AOE1);
        proyecto.setAoe2(AOE2);
        proyecto.setAoe3(AOE3);
        proyecto.setAoe4(AOE4);
        proyecto.setAoe5(AOE5);
        proyecto.setAoe6(AOE6);
        proyecto.setAoe7(AOE7);

        if (memoria != null && memoria.length > 0) {
            proyecto.setMemoria(memoria);
        }
        if (especificaciones != null && especificaciones.length > 0) {
            proyecto.setEspecificaciones(especificaciones);
        }
        if (presupuesto != null && presupuesto.length > 0) {
            proyecto.setPresupuestos(presupuesto);
        }
        return proyectoRepository.save(proyecto);
    }

    private void update (Proyecto proyecto){
        proyectoRepository.save(proyecto);
    }

    private void validateProjectData(String titulo, String nombrecorto, byte[] memoria, String nombresolicitante, String correo, String unidad, String select, int importancia, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, List<String> selectedValues) {
        if (titulo.isEmpty() || nombrecorto.isEmpty() || memoria.length == 0 || nombresolicitante.isEmpty() || correo.isEmpty() || unidad.isEmpty() || select.isEmpty() || importancia <= 0 || interesados.isEmpty() || financiacion == 0 || alcance.isEmpty() || fechaObjetivo == null || normativa.isEmpty()) {
            throw new IllegalArgumentException("Por favor, complete todos los campos obligatorios marcados con asterisco.");
        }
        if (selectedValues.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos uno de los objetivos estratégicos.");
        }
        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (importancia < 0 || importancia > 5) {
            throw new IllegalArgumentException("La importancia debe estar entre 0 y 5.");
        }
        if (financiacion < 0) {
            throw new IllegalArgumentException("La financiación no puede ser negativa.");
        }
        if (fechaObjetivo.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha objetivo no puede ser anterior a la fecha actual.");
        }
    }

    private void validateUpdateData(String titulo, String nombrecorto, byte[] memoria, String nombresolicitante, String correo, String unidad, String select, int importancia, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, List<String> selectedValues) {
        if (titulo.isEmpty() || nombrecorto.isEmpty() || nombresolicitante.isEmpty() || correo.isEmpty() || unidad.isEmpty() || select.isEmpty() || importancia == 0 || interesados.isEmpty() || financiacion == 0 || alcance.isEmpty() || fechaObjetivo == null || normativa.isEmpty()) {
            throw new IllegalArgumentException("Por favor, complete todos los campos obligatorios marcados con asterisco.");
        }
        if (selectedValues.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos uno de los objetivos estratégicos.");
        }
        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (importancia < 0 || importancia > 5) {
            throw new IllegalArgumentException("La importancia debe estar entre 0 y 5.");
        }
        if (financiacion < 0) {
            throw new IllegalArgumentException("La financiación no puede ser negativa.");
        }
    }

    public Proyecto save(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public Optional<Proyecto> get(Long id) {
        return proyectoRepository.findById(id);
    }


    public Page<Proyecto> list(Pageable pageable) {
        return proyectoRepository.findAll(pageable);
    }

    public void delete(Long id) {
        proyectoRepository.deleteById(id);
    }

    public void updateCalTecnica(String nombreCorto, Double calTecnica) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        if (proyecto.getCalDisponibilidad() != null && proyecto.getCalOportunidad() != null) {
            Ponderaciones ponderaciones = ponderacionesRepository.findById(1)
                    .orElseThrow(() -> new IllegalArgumentException("No se han encontrado las ponderaciones"));
            double calFinal = calTecnica * ponderaciones.getPonTecnica() +
                    proyecto.getCalOportunidad() * ponderaciones.getPonOportunidad() +
                    proyecto.getCalDisponibilidad() * ponderaciones.getPonDisponibilidad();
            proyecto.setCalFinal(calFinal);
            proyecto.setCalificado(true);
        }

        proyecto.setCalTecnica(calTecnica);
        proyectoRepository.save(proyecto);
    }

    public void updateCalOportunidad(String nombreCorto, Double calOportunidad) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        if (proyecto.getCalDisponibilidad() != null && proyecto.getCalTecnica() != null) {
            Ponderaciones ponderaciones = ponderacionesRepository.findById(1)
                    .orElseThrow(() -> new IllegalArgumentException("No se han encontrado las ponderaciones"));
            double calFinal = proyecto.getCalTecnica() * ponderaciones.getPonTecnica() +
                    calOportunidad * ponderaciones.getPonOportunidad() +
                    proyecto.getCalDisponibilidad() * ponderaciones.getPonDisponibilidad();
            proyecto.setCalFinal(calFinal);
            proyecto.setCalificado(true);
            System.out.println("Calificado se ha puesto a true");
        }

        proyecto.setCalOportunidad(calOportunidad);
        proyectoRepository.save(proyecto);
    }


    public record SampleItem(String value, String label, Boolean disabled) {
    }

    public void setSelectSponsors(ComboBox<String> select) {
        List<String> promotores = userRepository.findPromotor();

        select.setItems(promotores);
    }

    public void setSelectProjects(ComboBox<String> comboBox) {
        List<String> proyectos = proyectoRepository.findPendingProjects();

        comboBox.setItems(proyectos);
    }

    public List<Proyecto> searchProjects(String nombreCorto, String nombreSolicitante, String estado, DatePicker fechaSolicitud) {
        Specification<Proyecto> spec = Specification.where(null);

        if (nombreCorto != null && !nombreCorto.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombreCorto")), "%" + nombreCorto.toLowerCase() + "%"));
        }

        if (nombreSolicitante != null && !nombreSolicitante.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombreSolicitante")), "%" + nombreSolicitante.toLowerCase() + "%"));
        }

        if (estado != null && !estado.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("estado")), "%" + estado.toLowerCase() + "%"));
        }

        if (fechaSolicitud != null && fechaSolicitud.getValue() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("fechaCreado"), fechaSolicitud.getValue()));
        }

        return proyectoRepository.findAll(spec);
    }

    public List<Proyecto> searchQualifiedProjects() {
        return proyectoRepository.findByCalificadoTrue();
    }


    public String getDownloadUrl(String nombreProyecto, int option) {
        return "/api/downloads?nombreProyecto=" + nombreProyecto + "&option=" + option;
    }

    public Double getCalificacionTecnica(String nombreCorto) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        return proyecto.getCalTecnica();  // Devuelve la calificación técnica o null si no tiene
    }

    public Double getCalificacionOportunidad(String nombreCorto) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        return proyecto.getCalOportunidad();  // Devuelve la calificación técnica o null si no tiene
    }

    public Double getCalificacionDisponibilidad(String nombreCorto) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        return proyecto.getCalDisponibilidad();  // Devuelve la calificación de financiación o null si no tiene
    }

    public Double getFinanciacionAportada(String nombreCorto) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        return proyecto.getFinanciacion();  // Devuelve la financiación aportada o null si no tiene
    }

    public void updateCalDisponibilidad(String nombreCorto, Double calDisponibilidad, int recursosHumanos, double financiacion) {
        Proyecto proyecto = proyectoRepository.findByNombreCorto(nombreCorto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con nombre_corto: " + nombreCorto));

        if (proyecto.getCalTecnica() != null && proyecto.getCalOportunidad() != null) {
            Ponderaciones ponderaciones = ponderacionesRepository.findById(1)
                    .orElseThrow(() -> new IllegalArgumentException("No se han encontrado las ponderaciones"));
            double calFinal = proyecto.getCalTecnica() * ponderaciones.getPonTecnica() +
                    proyecto.getCalOportunidad() * ponderaciones.getPonOportunidad() +
                    calDisponibilidad * ponderaciones.getPonDisponibilidad();
            proyecto.setCalFinal(calFinal);
            proyecto.setCalificado(true);
        }

        proyecto.setCalDisponibilidad(calDisponibilidad);
        proyecto.setRecursosHumanosNecesarios(recursosHumanos);
        proyecto.setFinanciacionNecesaria(financiacion);
        proyectoRepository.save(proyecto);
    }

    public void updateAllCalFinal (){
        List<Proyecto> proyectos = proyectoRepository.findAll();
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getCalTecnica() != null && proyecto.getCalOportunidad() != null && proyecto.getCalDisponibilidad() != null) {
                Ponderaciones ponderaciones = ponderacionesRepository.findById(1)
                        .orElseThrow(() -> new IllegalArgumentException("No se han encontrado las ponderaciones"));
                double calFinal = proyecto.getCalTecnica() * ponderaciones.getPonTecnica() +
                        proyecto.getCalOportunidad() * ponderaciones.getPonOportunidad() +
                        proyecto.getCalDisponibilidad() * ponderaciones.getPonDisponibilidad();
                proyecto.setCalFinal(calFinal);
            }
        }
        proyectoRepository.saveAll(proyectos);
    }

    public void updateProjectStatus(Proyecto proyecto, String newStatus) {
        switch (newStatus) {
            case "Aceptado":
                proyecto.setEstado("Aceptado");
                break;
            case "Rechazado":
                proyecto.setEstado("Rechazado");
                break;
            case "Pendiente":
                proyecto.setEstado("Pendiente");
                break;
            default:
                throw new IllegalArgumentException("Estado inválido: " + newStatus);
        }
        save(proyecto);
    }

    public Proyecto findById(long id) {
        return proyectoRepository.findById(id).orElseGet(Proyecto::new);
    }
    // Método para obtener la convocatoria activa
    public Convocatoria getConvocatoriaActual() {
        LocalDate hoy = LocalDate.now();
        return convocatoriaRepository.findByFechaAperturaBeforeAndFechaCierreAfter(hoy, hoy)
                .orElseThrow(() -> new IllegalArgumentException("No hay convocatorias activas actualmente."));
    }
}
