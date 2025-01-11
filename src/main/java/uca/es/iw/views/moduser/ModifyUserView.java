package uca.es.iw.views.moduser;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.services.UserService;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RolesAllowed("ADMIN")
@Route(value = "modify-user/:userID", layout = uca.es.iw.views.MainLayout.class)
public class ModifyUserView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final UserService userService;
    private final I18NProvider i18nProvider;

    private Long userId; // Para almacenar el ID del usuario a modificar
    private Avatar avatar;
    private byte[] profilePictureData; // Almacena temporalmente la imagen cargada

    private TextField nameField;
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private EmailField emailField;

    @Autowired
    public ModifyUserView(UserService userService, I18NProvider i18nProvider) {
        this.userService = userService;
        this.i18nProvider = i18nProvider;

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3(i18nProvider.getTranslation("modify_user.title", getLocale()));
        HorizontalLayout layoutRow = new HorizontalLayout();

        // Formulario
        FormLayout formLayout = new FormLayout();
        nameField = new TextField(i18nProvider.getTranslation("modify_user.name", getLocale()));
        usernameField = new TextField(i18nProvider.getTranslation("modify_user.username", getLocale()));
        passwordField = new PasswordField(i18nProvider.getTranslation("modify_user.password", getLocale()));
        roleComboBox = new ComboBox<>(i18nProvider.getTranslation("modify_user.role", getLocale()));
        emailField = new EmailField(i18nProvider.getTranslation("modify_user.email", getLocale()));

        roleComboBox.setItems(
                i18nProvider.getTranslation("modify_user.role.user", getLocale()),
                i18nProvider.getTranslation("modify_user.role.admin", getLocale()),
                i18nProvider.getTranslation("modify_user.role.otp", getLocale()),
                i18nProvider.getTranslation("modify_user.role.cio", getLocale()),
                i18nProvider.getTranslation("modify_user.role.promotor", getLocale())
        );
        roleComboBox.setPlaceholder(i18nProvider.getTranslation("modify_user.role_placeholder", getLocale()));

        // Avatar
        avatar = new Avatar();
        avatar.setName(i18nProvider.getTranslation("modify_user.avatar_preview", getLocale()));

        // Componente de subida de imágenes
        Upload upload = getUpload();

        // Botón para guardar
        Button saveButton = new Button(i18nProvider.getTranslation("modify_user.save", getLocale()));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveUser());

        Button deleteButton = new Button(i18nProvider.getTranslation("modify_user.clear_button", getLocale()));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Crear el diálogo de confirmación
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle(i18nProvider.getTranslation("modify_user.title", getLocale()));

        // Mensaje en el diálogo
        Div message = new Div();
        message.setText(i18nProvider.getTranslation("modify_user.message", getLocale()));
        confirmDialog.add(message);

        // Botón "Cancelar"
        Button cancelButton = new Button(i18nProvider.getTranslation("modify_user.cancel_button", getLocale()), e -> confirmDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Botón "Eliminar"
        Button confirmButton = new Button(i18nProvider.getTranslation("modify_user.confirm_button", getLocale()), e -> {
            try {
                userService.delete(userId); // Llama al método de eliminación
                Notification.show(i18nProvider.getTranslation("modify_user.success_notification", getLocale()), 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("search-users");
            } catch (Exception ex) {
                Notification.show(i18nProvider.getTranslation("modify_user.error_notification", getLocale()) + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            } finally {
                confirmDialog.close();
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        // Añadir botones al diálogo
        HorizontalLayout dialogButtons = new HorizontalLayout(cancelButton, confirmButton);
        confirmDialog.getFooter().add(dialogButtons);

        // Configurar evento para abrir el diálogo
        deleteButton.addClickListener(event -> confirmDialog.open());



        // Diseño principal
        formLayout.add(nameField, usernameField, passwordField, roleComboBox, emailField);
        layoutRow.add(saveButton, deleteButton);
        layoutColumn2.add(h3, formLayout, avatar, upload, layoutRow);



        getContent().add(layoutColumn2);
        getContent().setAlignItems(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Obtén el ID del usuario desde la ruta
        String userIdStr = event.getRouteParameters().get("userID").orElse(null);
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
                // Carga los datos del usuario
                userService.get(userId).ifPresentOrElse(
                        this::populateForm,
                        () -> {
                            Notification.show(i18nProvider.getTranslation("modify_user.user_not_found", getLocale()), 3000, Notification.Position.MIDDLE);
                            event.forwardTo("search-users"); // Redirige si el usuario no existe
                        }
                );
            } catch (NumberFormatException e) {
                Notification.show(i18nProvider.getTranslation("modify_user.invalid_user_id", getLocale()), 3000, Notification.Position.MIDDLE);
                event.forwardTo("search-users");
            }
        }
    }

    private Upload getUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFileSize(2 * 1024 * 1024);

        upload.addSucceededListener(event -> {
            try {
                profilePictureData = buffer.getInputStream().readAllBytes();
                avatar.setImageResource(new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(profilePictureData)));
                Notification.show(i18nProvider.getTranslation("modify_user.success", getLocale()), 3000, Notification.Position.MIDDLE);
            } catch (IOException e) {
                Notification.show(i18nProvider.getTranslation("modify_user.error", getLocale()), 3000, Notification.Position.MIDDLE);
            }
        });

        upload.addFileRemovedListener(event -> {
            avatar.setImageResource(null);
            profilePictureData = null;
        });

        return upload;
    }

    private void clearForm() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        avatar.setImageResource(null);
        profilePictureData = null;
    }

    private void populateForm(uca.es.iw.data.User user) {
        nameField.setValue(user.getName() != null ? user.getName() : "");
        usernameField.setValue(user.getUsername() != null ? user.getUsername() : "");
        emailField.setValue(user.getEmail() != null ? user.getEmail() : "");

        if (user.getProfilePicture() != null) {
            profilePictureData = user.getProfilePicture();
            avatar.setImageResource(new StreamResource("profilePicture",
                    () -> new ByteArrayInputStream(profilePictureData)));
        }
    }

    private void saveUser() {
        if (nameField.getValue().isEmpty() || usernameField.getValue().isEmpty() || emailField.getValue().isEmpty()) {
            Notification.show(i18nProvider.getTranslation("modify_user.missing_fields", getLocale()), 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            userService.updateUser(
                    userId,
                    nameField.getValue(),
                    usernameField.getValue(),
                    passwordField.getValue().isEmpty() ? null : passwordField.getValue(),
                    emailField.getValue(),
                    profilePictureData,
                    roleComboBox.getValue()
            );
            Notification.show(i18nProvider.getTranslation("modify_user.success", getLocale()), 3000, Notification.Position.MIDDLE);
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show(i18nProvider.getTranslation("modify_user.error", getLocale()) + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
        //volver a la vista de búsqueda de usuarios
        UI.getCurrent().navigate("search-users");
    }
}
