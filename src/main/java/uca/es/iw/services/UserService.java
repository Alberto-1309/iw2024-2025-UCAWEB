package uca.es.iw.services;

import java.util.HashSet;
import java.util.Optional;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uca.es.iw.data.User;
import uca.es.iw.data.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import uca.es.iw.data.Role;


@Service
public class UserService {



    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

        switch (role) {
            case "ADMIN":
                user.setRoles(Set.of(Role.ADMIN, Role.USER, Role.CIO, Role.PROMOTOR));
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
}
