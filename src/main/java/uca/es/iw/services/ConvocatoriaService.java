package uca.es.iw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uca.es.iw.data.Convocatoria;
import uca.es.iw.data.ConvocatoriaRepository;
import uca.es.iw.data.Role;
import uca.es.iw.data.User;

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
    // Crear una nueva convocatoria
    public Convocatoria createConvocatoria(String nombre, String objetivo, LocalDate fechaApertura,
                                           LocalDate fechaCierre, double presupuestoTotal, int cupoRecursosHumanos) {
        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setNombre(nombre);
        convocatoria.setObjetivo(objetivo);
        convocatoria.setFechaApertura(fechaApertura);
        convocatoria.setFechaCierre(fechaCierre);
        convocatoria.setPresupuestoTotal(presupuestoTotal);
        convocatoria.setCupoRecursosHumanos(cupoRecursosHumanos);
        Convocatoria savedConvocatoria = convocatoriaRepository.save(convocatoria);
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
                                           LocalDate fechaCierre, double presupuestoTotal, int cupoRecursosHumanos) {
        Convocatoria convocatoria = getConvocatoriaById(id);
        convocatoria.setNombre(nombre);
        convocatoria.setObjetivo(objetivo);
        convocatoria.setFechaApertura(fechaApertura);
        convocatoria.setFechaCierre(fechaCierre);
        convocatoria.setPresupuestoTotal(presupuestoTotal);
        convocatoria.setCupoRecursosHumanos(cupoRecursosHumanos);
        Convocatoria updatedConvocatoria = convocatoriaRepository.save(convocatoria);
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
                emailService.sendEmail(user.getEmail(), subject, body);
            }
        }
    }
}