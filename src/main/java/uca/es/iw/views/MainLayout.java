package uca.es.iw.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.html.Image;


import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import uca.es.iw.data.User;
import uca.es.iw.security.AuthenticatedUser;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addGlobalFooter(); // Añade el footer global fijo
        adjustContentForFooter(); // Ajusta el contenido para el footer
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        // Agregar un logo
        Image logo = new Image("images/uca-logo.png", "UCA Logo");
        logo.setHeight("40px"); // Ajusta el tamaño del logo

        // Estilo del título
        viewTitle = new H1("Universidad de Cádiz");
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.getStyle().set("color", "white"); // Cambia el color aquí

        // Crear el interruptor de idioma
        String languageText = getTranslation("menu.language");
        String spanishText = getTranslation("menu.spanish");
        String englishText = getTranslation("menu.english");

        MenuBar languageMenu = new MenuBar();
        languageMenu.setThemeName("tertiary-inline contrast");
        languageMenu.getStyle()
                .set("background-color", "#005877")
                .set("border", "none");

        // Crear el "MenuItem" principal con texto blanco
        MenuItem languageMenuItem = languageMenu.addItem(languageText);
        languageMenuItem.getElement().getStyle()
                .set("color", "white")
                .set("font-weight", "bold");

        languageMenuItem.getElement().appendChild(new Icon("lumo", "dropdown").getElement());

        // Submenú con elementos en negro
        MenuItem spanishItem = languageMenuItem.getSubMenu().addItem(spanishText, e -> switchLanguage(new Locale("es")));
        MenuItem englishItem = languageMenuItem.getSubMenu().addItem(englishText, e -> switchLanguage(new Locale("en")));

        // Aplicar estilos a los elementos del submenú
        spanishItem.getElement().getStyle().set("color", "black");
        englishItem.getElement().getStyle().set("color", "black");

        // Crear un contenedor horizontal para el header
        HorizontalLayout headerLayout = new HorizontalLayout(toggle, logo, viewTitle, languageMenu);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setSpacing(true);
        headerLayout.getStyle()
                .set("background-color", "#005877")
                .set("padding", "10px");

        headerLayout.expand(viewTitle); // Hace que el título ocupe todo el espacio disponible
        addToNavbar(headerLayout);
    }



    private void addDrawerContent() {
        Span appName = new Span("IW");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            // Determinar la clave de traducción según la ruta o clase de vista
            String translationKey = switch (entry.title()) {
                case "WelcomeView" -> "welcome.title";
                case "ConvocatoriaView" -> "call.title";
                default -> null;
            };
            // Si hay una clave de traducción, intenta obtener la traducción; de lo contrario, usa el título original
            String translatedTitle = (translationKey != null)
                    ? getTranslation(translationKey)
                    : entry.title();
            // Crear la entrada del menú con el título traducido o el título original
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(translatedTitle, entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(translatedTitle, entry.path()));
            }
        });
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);

            String viewProfileText = getTranslation("menu.view_profile");
            String logoutText = getTranslation("menu.logout");
            userName.getSubMenu().addItem(viewProfileText, e -> getUI().ifPresent(ui -> ui.navigate("profile")));
            userName.getSubMenu().addItem(logoutText, e -> authenticatedUser.logout());

            layout.add(userMenu);
        } else {
            String signInText = getTranslation("menu.sign_in");
            Anchor loginLink = new Anchor("login", signInText);
            layout.add(loginLink);
        }

        return layout;
    }

    private void addGlobalFooter() {
        // Crear el footer global
        Footer globalFooter = new Footer();
        globalFooter.getStyle()
                .set("background-color", "#005877") // Mismo color que el header
                .set("color", "white")
                .set("text-align", "center")
                .set("padding", "10px")
                .set("position", "fixed") // Fija el footer al fondo
                .set("bottom", "0")
                .set("width", "100%");
        globalFooter.add(new Span("© 2024 Universidad de Cádiz - Todos los derechos reservados."));

        // Crear un contenedor para el footer fuera del drawer
        Div footerContainer = new Div(globalFooter);
        footerContainer.getStyle()
                .set("z-index", "100") // Asegúrate de que esté por encima del contenido
                .set("width", "100%");

        // Añade el contenedor al final del MainLayout
        getElement().appendChild(footerContainer.getElement());
    }


    private void adjustContentForFooter() {
        getElement().getStyle().set("padding-bottom", "50px"); // Altura del footer
    }




    private void switchLanguage(Locale locale) {
        VaadinSession.getCurrent().setLocale(locale);
        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("locale", locale);

        // Asegurarse de recargar la UI
        getUI().ifPresent(ui -> ui.access(() -> {
            ui.getPage().reload();
        }));
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        // Obtener el título dinámico desde la vista actual
        String pageTitle = getTranslation(getCurrentPageTitle());
        viewTitle.setText(pageTitle);
        getUI().ifPresent(ui -> ui.getPage().setTitle(pageTitle));
    }

    private String getCurrentPageTitle() {
        String className = getContent().getClass().getSimpleName();
        switch (className) {
            case "ProfileView":
                return "profile.title";
            case "LoginView":
                return "login.title";
            case "WelcomeView":
                return "welcome.title";
            case "ConvocatoriaView":
                return "call.title";
            default:
                return "app.default_title";
        }
    }
}