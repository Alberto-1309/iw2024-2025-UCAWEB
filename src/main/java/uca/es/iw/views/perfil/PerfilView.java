package uca.es.iw.views.perfil;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import uca.es.iw.security.AuthenticatedUser;
import uca.es.iw.data.User;
import uca.es.iw.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Route(value = "perfil", layout = uca.es.iw.views.MainLayout.class)
@PageTitle("Perfil de Usuario")
@RolesAllowed("ROLE_USER")
public class PerfilView extends VerticalLayout {

    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Campos del formulario
    private final TextField username = new TextField("Nombre de usuario");
    private final TextField name = new TextField("Nombre completo");
    private final TextField roles = new TextField("Rol de usuario");
    private final PasswordField password = new PasswordField("Nueva contraseña");
    private final Image profilePicture = new Image();
    private final Upload imageUpload;

    private byte[] uploadedImage; // Para guardar la imagen subida

    public PerfilView(AuthenticatedUser authenticatedUser, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            // Configurar campos
            username.setValue(user.getUsername());
            name.setValue(user.getName());
            roles.setValue(user.getRoles().toString());
            roles.setReadOnly(true); // El rol es solo informativo

            // Configurar imagen de perfil
            if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
                profilePicture.setSrc(new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(user.getProfilePicture())));
            } else {
                profilePicture.setSrc("https://via.placeholder.com/150");
            }
            profilePicture.setWidth("150px");
            profilePicture.setHeight("150px");
        }

        // Configurar la carga de imagen con MemoryBuffer
        MemoryBuffer buffer = new MemoryBuffer();
        imageUpload = new Upload(buffer);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png");
        imageUpload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                uploadedImage = inputStream.readAllBytes();
                profilePicture.setSrc(new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(uploadedImage)));
                Notification.show("Imagen cargada correctamente.");
            } catch (IOException e) {
                Notification.show("Error al cargar la imagen: " + e.getMessage());
            }
        });

        Button saveButton = new Button("Guardar cambios", event -> {
            maybeUser.ifPresent(user -> {
                // Actualizar nombre de usuario
                String newUsername = username.getValue();
                if (!newUsername.isEmpty() && !newUsername.equals(user.getUsername())) {
                    user.setUsername(newUsername);
                }

                // Actualizar nombre completo
                user.setName(name.getValue());

                // Actualizar contraseña
                if (!password.getValue().isEmpty()) {
                    String hashedPassword = encriptarContrasena(password.getValue());
                    user.setHashedPassword(hashedPassword);
                }

                // Actualizar imagen
                if (uploadedImage != null) {
                    user.setProfilePicture(uploadedImage);
                }

                // Guardar cambios en la base de datos
                userService.updateUserData(user);

                // Actualizar la sesión
                authenticatedUser.setUsername(user); // Actualiza el usuario en la sesión

                // Recargar página para mostrar cambios
                UI.getCurrent().getPage().reload();

                // Notificación de éxito
                Notification.show("Perfil actualizado correctamente.");
            });
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Diseño
        setWidth("100%");
        setAlignItems(Alignment.CENTER);

        username.setWidth("100%");
        name.setWidth("100%");
        password.setWidth("100%");
        roles.setWidth("100%");

        // Desactivando autocompletado de la nueva contraseña con un nombre ficticio
        password.getElement().setAttribute("autocomplete", "new-password");
        password.getElement().setAttribute("name", "new-password-field");

        // Desactivar autocompletado en todo el formulario
        this.getElement().setAttribute("autocomplete", "new-password");

        // Agregar un campo oculto para evitar que el navegador complete automáticamente
        TextField hiddenField = new TextField();
        hiddenField.setVisible(false);
        hiddenField.setId("hidden-password-field");
        hiddenField.getElement().setAttribute("autocomplete", "off");
        add(hiddenField);

        // Agregar componentes al layout
        add(username, name, roles, password, profilePicture, imageUpload, saveButton);
    }

    private String encriptarContrasena(String password) {
        return passwordEncoder.encode(password);
    }
}