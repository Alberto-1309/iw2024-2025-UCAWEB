package uca.es.iw.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.notification.Notification;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uca.es.iw.data.User;
import uca.es.iw.data.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import uca.es.iw.data.Role;
import org.springframework.beans.factory.annotation.Autowired; // Import para la inyección


@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String SPONSORS_URL = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }




    public User createUser(String name, String username, String rawPassword, String email, byte[] profilePicture, String role) {
        validateUserData(name, username, rawPassword, email, role);
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(rawPassword)); // Hashear la contraseña
        user.setEmail(email);
        user.setProfilePicture(profilePicture);

        roleSetter(user, role);
        sendWelcomeEmail(user);
        return repository.save(user);
    }

    private void validateUserData(String name, String username, String password, String email, String role) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (username == null || username.trim().isEmpty() || username.contains(" ")) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío ni contener espacios.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Debe seleccionar un rol válido.");
        }
    }

    private void validateUpdateData(String name, String username, String password, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (username == null || username.trim().isEmpty() || username.contains(" ")) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío ni contener espacios.");
        }
        if (password != null) {
            if (password.length() < 8) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
            }
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
    }



    public List<User> searchUsers(String username, String email) {
        Specification<User> spec = Specification.where(null);

        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        return repository.findAll(spec);
    }

    public void updateUser(Long id, String name, String username, String password, String email, byte[] profilePicture, String role) {
        validateUpdateData(name, username, password, email);
        User user = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        if (password != null) {
            user.setHashedPassword(passwordEncoder.encode(password));
        }
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }
        if (role != null) {
            deleteRoles(id);
            roleSetter(user, role);
        }


        repository.save(user);
    }

    public void roleSetter (User user, String role) {
        switch (role) {
            case "ADMIN":
                user.setRoles(Set.of(Role.ADMIN, Role.USER, Role.CIO, Role.PROMOTOR, Role.OTP));
                break;
            case "USER":
                user.setRoles(Set.of(Role.USER));
                break;
            case "OTP":
                user.setRoles(Set.of(Role.OTP, Role.USER));
                break;
            case "CIO":
                user.setRoles(Set.of(Role.CIO, Role.USER));
                break;
            case "PROMOTOR":
                user.setRoles(Set.of(Role.PROMOTOR, Role.USER));
                break;
            default:
                break;
        }
    }

    @Transactional
    public void deleteRoles(Long userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.getRoles().clear(); // Limpia todos los roles
            //repository.save(user); // Guarda los cambios
        } else {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe.");
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.getRoles().clear(); // Limpia los roles asociados al usuario
            repository.delete(user); // Elimina el usuario
        } else {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe.");
        }
    }
    public User updateUserData(User user) {
        // Verificar si el usuario existe en la base de datos
        Optional<User> existingUserOptional = repository.findById(user.getId());
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Actualizar nombre de usuario y nombre completo
            if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
                existingUser.setUsername(user.getUsername());
            }
            existingUser.setName(user.getName());

            // Actualizar la contraseña solo si fue modificada
            if (user.getHashedPassword() != null && !user.getHashedPassword().equals(existingUser.getHashedPassword())) {
                existingUser.setHashedPassword(user.getHashedPassword());

            }

            // Actualizar la imagen de perfil si existe
            if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
                existingUser.setProfilePicture(user.getProfilePicture());
            }

            // Guardar el usuario actualizado en la base de datos
            return repository.save(existingUser);
        } else {
            throw new IllegalArgumentException("El usuario no existe.");
        }
    }

    public void syncSponsors() {
        try {
            // Obtener datos desde el servicio externo
            JSONArray externalData = fetchSponsorsData();

            // Recuperar usuarios actuales de la base de datos
            List<User> usuarios = repository.findAll(); // Método que obtiene todos los usuarios

            // Iterar sobre los usuarios y verificar si están en la lista
            for (User usuario : usuarios) {
                boolean isPromoter = isInSponsorList(usuario, externalData);

                // Si el usuario está en la lista, actualizar su rol
                if (isPromoter) {
                    roleSetter(usuario, "PROMOTOR");
                    repository.save(usuario); // Guarda los cambios en la base de datos
                }
                if (!isPromoter && usuario.getRoles().contains(Role.PROMOTOR) && !usuario.getRoles().contains(Role.ADMIN)) {
                    deleteRoles(usuario.getId());
                    roleSetter(usuario, "USER");
                    repository.save(usuario);
                }
            }

            System.out.println("Sincronización completada.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error durante la sincronización: " + e.getMessage());
        }
    }

    private JSONArray fetchSponsorsData() throws Exception {
        URL url = new URL(SPONSORS_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // Parsear el JSON de la respuesta
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("data");
        } else {
            throw new RuntimeException("Error al obtener datos del servicio: " + conn.getResponseMessage());
        }
    }

    private boolean isInSponsorList(User usuario, JSONArray externalData) {
        try {
            for (int i = 0; i < externalData.length(); i++) {
                JSONObject sponsor = externalData.getJSONObject(i);

                // Adaptar el ID externo (ejemplo: "u00000001" -> 1)
                String externalIdString = sponsor.getString("id").substring(1); // Quitar la "u"
                int externalId = Integer.parseInt(externalIdString);

                // Verificar si coinciden el ID y el nombre
                if (usuario.getId() == externalId &&
                        usuario.getName().equalsIgnoreCase(sponsor.getString("nombre"))) {
                    return true;
                }
            }
        } catch (JSONException e) {
            System.err.println("Error al procesar los datos del patrocinador: " + e.getMessage());
        }
        return false;
    }
    private void sendWelcomeEmail(User user) {
        String subject = "¡Bienvenido a la plataforma!";
        String body = "Hola " + user.getName() + ",\n\n" +
                "Tu cuenta ha sido creada exitosamente. Ahora puedes iniciar sesión con tu nombre de usuario: " + user.getUsername() + ".\n\n" +
                "¡Gracias por unirte!\n\n" +
                "Saludos,\nEl equipo.";

        emailService.sendEmail(user.getEmail(), subject, body);
    }
    public void deletePersonalData(User user) {
        user.setName("");
        user.setProfilePicture(null);
        user.setEmail("");
        repository.save(user);
    }
    public List<User> getAllUsers() {
        return repository.findAll(); // Recupera todos los usuarios de la base de datos
    }

}
