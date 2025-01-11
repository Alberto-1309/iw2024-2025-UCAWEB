package uca.es.iw.views.adduser;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.security.AuthenticatedUser;
import uca.es.iw.services.UserService;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.i18n.I18NProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@AnonymousAllowed
@Route(value = "register", layout = uca.es.iw.views.MainLayout.class)
public class RegisterUserView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final I18NProvider i18nProvider;
    private Avatar avatar;
    private byte[] profilePictureData;

    @Autowired
    private UserService userService;

    private TextField nameField;
    private TextField usernameField;
    private PasswordField passwordField;
    private String role;
    private EmailField emailField;

    @Autowired
    public RegisterUserView(AuthenticatedUser authenticatedUser, I18NProvider i18nProvider) {
        this.authenticatedUser = authenticatedUser;
        this.i18nProvider = i18nProvider;

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3(i18nProvider.getTranslation("add_user.header", getLocale()));

        FormLayout formLayout = new FormLayout();
        nameField = new TextField(i18nProvider.getTranslation("add_user.name", getLocale()));
        usernameField = new TextField(i18nProvider.getTranslation("add_user.username", getLocale()));
        passwordField = new PasswordField(i18nProvider.getTranslation("add_user.password", getLocale()));
        role = "USER";
        emailField = new EmailField(i18nProvider.getTranslation("add_user.email", getLocale()));

        avatar = new Avatar();
        avatar.setName(i18nProvider.getTranslation("add_user.avatar_preview", getLocale()));

        Upload upload = getUpload();

        Button saveButton = new Button(i18nProvider.getTranslation("add_user.save", getLocale()));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveUser(
                nameField.getValue(),
                usernameField.getValue(),
                passwordField.getValue(),
                role,
                emailField.getValue()
        ));

        formLayout.add(nameField, usernameField, passwordField, emailField);
        layoutColumn2.add(h3, formLayout, avatar, upload, saveButton);

        getContent().add(layoutColumn2);
        getContent().setAlignItems(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            event.forwardTo("");
        }
    }

    private Upload getUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFileSize(2 * 1024 * 1024);

        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            try {
                profilePictureData = inputStream.readAllBytes();
                avatar.setImageResource(new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(profilePictureData)));
                Notification.show(i18nProvider.getTranslation("add_user.image_success", getLocale()), 3000, Notification.Position.MIDDLE);
            } catch (IOException e) {
                Notification.show(i18nProvider.getTranslation("add_user.image_error", getLocale()), 3000, Notification.Position.MIDDLE);
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

    private void saveUser(String name, String username, String password, String role, String email) {
        try {
            userService.createUser(name, username, password, email, profilePictureData, role);
            Notification.show(i18nProvider.getTranslation("add_user.save_success", getLocale()), 3000, Notification.Position.MIDDLE);
            clearForm();
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show(i18nProvider.getTranslation("add_user.save_error", getLocale()) + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
}