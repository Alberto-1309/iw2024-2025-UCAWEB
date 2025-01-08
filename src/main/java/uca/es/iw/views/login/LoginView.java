package uca.es.iw.views.login;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import uca.es.iw.security.AuthenticatedUser;

@AnonymousAllowed
@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        // Crear y configurar el formulario de inicio de sesión
        var login = new LoginForm();
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        login.setI18n(createLoginI18n());
        add(
                login,
                new Anchor("register", getTranslation("login.register")) // Traducción del enlace de registro
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            event.forwardTo("");
        }
    }

    private LoginI18n createLoginI18n() {
        var i18n = LoginI18n.createDefault();
        var locale = VaadinSession.getCurrent().getLocale();

        var header = new LoginI18n.Header();
        header.setTitle(getTranslation("login.title", locale));
        header.setDescription(getTranslation("login.description", locale));
        i18n.setHeader(header);

        var form = new LoginI18n.Form();
        form.setTitle(getTranslation("login.title", locale));
        form.setUsername(getTranslation("login.username", locale));
        form.setPassword(getTranslation("login.password", locale));
        form.setSubmit(getTranslation("login.submit", locale));
        form.setForgotPassword(getTranslation("login.forgot_password", locale));
        i18n.setForm(form);

        var errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(getTranslation("login.error.title", locale));
        errorMessage.setMessage(getTranslation("login.error.message", locale));
        i18n.setErrorMessage(errorMessage);

        i18n.setAdditionalInformation(null);

        return i18n;
    }
}