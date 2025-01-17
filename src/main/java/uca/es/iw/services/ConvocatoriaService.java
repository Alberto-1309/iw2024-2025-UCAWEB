package uca.es.iw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uca.es.iw.data.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConvocatoriaService {
    @Autowired
    private ConvocatoriaRepository convocatoriaRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private RecursosService recursosService;
    // Crear una nueva convocatoria
    public Convocatoria createConvocatoria(String nombre, String objetivo, LocalDate fechaApertura,
                                           LocalDate fechaCierre, double presupuesto, int recursosHumanos) {
        // Crear la nueva convocatoria
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre(nombre);
        convocatoria.setObjetivo(objetivo);
        convocatoria.setFechaApertura(fechaApertura);
        convocatoria.setFechaCierre(fechaCierre);

        // Guardar la convocatoria
        Convocatoria savedConvocatoria = convocatoriaRepository.save(convocatoria);

        // Crear un objeto de recursos asociado a la convocatoria
        Recursos recursos = new Recursos();
        recursos.setPresupuestoTotal(presupuesto);
        recursos.setRecursosHumanos(recursosHumanos);
        recursos.setPresupuestoRestante(presupuesto);
        recursos.setRecursosHumanosRestantes(recursosHumanos);
        recursos.setIdConvocatoria(savedConvocatoria.getId()); // Establecer la relación entre recursos y convocatoria

        // Guardar el recurso asociado a la convocatoria
        recursosService.saveRecursos(recursos);

        // Enviar un correo electrónico de confirmación o notificación
        sendCreationEmailToUsers(savedConvocatoria);

        return savedConvocatoria;
    }

    private void sendCreationEmailToUsers(Convocatoria convocatoria) {
        String subject = "Nueva convocatoria creada: " + convocatoria.getNombre();
        String body = "Se ha creado una nueva convocatoria:\n\n" +
                "Nombre: " + convocatoria.getNombre() + "\n" +
                "Objetivo: " + convocatoria.getObjetivo() + "\n" +
                "Fecha de Apertura: " + convocatoria.getFechaApertura() + "\n" +
                "Fecha de Cierre: " + convocatoria.getFechaCierre() + "\n\n" +
                "Saludos,\nEl equipo.";
        sendEmailToUsers(subject, body);
    }
    public List<Convocatoria> getAllConvocatorias() {
        return convocatoriaRepository.findAll();
    }
    public Convocatoria getConvocatoriaById(Long id) {
        Optional<Convocatoria> convocatoria = convocatoriaRepository.findById(id);
        return convocatoria.orElseThrow(() -> new IllegalArgumentException("Convocatoria no encontrada"));
    }
    //Modificar una convocatoria
    public Convocatoria updateConvocatoria(Long id, String nombre, String objetivo, LocalDate fechaApertura,
                                           LocalDate fechaCierre, double presupuesto, int recursosHumanos) {
        Convocatoria convocatoria = getConvocatoriaById(id);
        convocatoria.setNombre(nombre);
        convocatoria.setObjetivo(objetivo);
        convocatoria.setFechaApertura(fechaApertura);
        convocatoria.setFechaCierre(fechaCierre);
        Convocatoria updatedConvocatoria = convocatoriaRepository.save(convocatoria);

        Recursos recursos = recursosService.getRecursosByConvocatoriaId(id);
        recursos.setPresupuestoTotal(presupuesto);
        recursos.setRecursosHumanos(recursosHumanos);
        recursos.setPresupuestoRestante(presupuesto);
        recursos.setRecursosHumanosRestantes(recursosHumanos);
        recursosService.saveRecursos(recursos);

        sendUpdateEmailToUsers(updatedConvocatoria);
        return updatedConvocatoria;
    }
    private void sendUpdateEmailToUsers(Convocatoria convocatoria) {
        String subject = "Convocatoria actualizada: " + convocatoria.getNombre();
        String body = "La convocatoria ha sido actualizada:\n\n" +
                "Nombre: " + convocatoria.getNombre() + "\n" +
                "Objetivo: " + convocatoria.getObjetivo() + "\n" +
                "Fecha de Apertura: " + convocatoria.getFechaApertura() + "\n" +
                "Fecha de Cierre: " + convocatoria.getFechaCierre() + "\n\n" +
                "Saludos,\nEl equipo.";
        sendEmailToUsers(subject, body);
    }
    //Eliminar una convocatoria
    public void deleteConvocatoria(Long id) {
        Convocatoria convocatoria = getConvocatoriaById(id);
        convocatoriaRepository.delete(convocatoria);
        sendDeletionEmailToUsers(convocatoria);
    }
    private void sendDeletionEmailToUsers(Convocatoria convocatoria) {
        String subject = "Convocatoria eliminada: " + convocatoria.getNombre();
        String body = "La convocatoria '" + convocatoria.getNombre() + "' ha sido eliminada.\n\n" +
                "Saludos,\nEl equipo.";

        sendEmailToUsers(subject, body);
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndNotifyEndedConvocatorias() {
        List<Convocatoria> convocatorias = convocatoriaRepository.findAll();
        LocalDate yesterday = LocalDate.now().minusDays(1);  // Obtener el día de ayer
        for (Convocatoria convocatoria : convocatorias) {
            if (convocatoria.getFechaCierre().equals(yesterday)) {
                notifyDeadlineEnd(convocatoria);
            }
        }
    }
    //Aviso el dia finalizacion de convocatoria
    public void notifyDeadlineEnd(Convocatoria convocatoria) {
        String subject = "Finalización del plazo: " + convocatoria.getNombre();
        String body = "El plazo para la convocatoria '" + convocatoria.getNombre() + "' ha terminado.\n\n" +
                "Saludos,\nEl equipo.";

        sendEmailToUsers(subject, body);
    }
    private void sendEmailToUsers(String subject, String body) {
        List<User> usersWithRoleUser = userService.getAllUsers().stream()
                .filter(user -> user.getRoles().contains(Role.USER))
                .collect(Collectors.toList());

        for (User user : usersWithRoleUser) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                if (!isValidEmail(user.getEmail())){
                    System.out.println("Correo electrónico no válido: " + user.getEmail());
                }

                emailService.sendEmail(user.getEmail(), subject, body);
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

}