package uca.es.iw.views.welcome;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = uca.es.iw.views.MainLayout.class)
@Menu(order = 0, icon = "line-awesome/svg/user.svg")
@AnonymousAllowed





public class WelcomeView extends VerticalLayout {
    private final I18NProvider i18nProvider;

    public WelcomeView(I18NProvider i18nProvider) {
        this.i18nProvider = i18nProvider;
        // Establecer el título de la página, que luego se pasará al MainLayout
        getUI().ifPresent(ui -> ui.getPage().setTitle(i18nProvider.getTranslation("welcome.title", getLocale())));
        // Configuración del layout principal
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Imagen del encabezado
        Image logo = new Image("images/uca-logo.png", "UCA Logo");
        logo.setWidth("150px");

        // Título principal
        H2 title = new H2(i18nProvider.getTranslation("welcome.main_title", getLocale()));
        title.getStyle().set("margin-top", "0");

        // Párrafo descriptivo
        Paragraph description = new Paragraph(
                i18nProvider.getTranslation("welcome.description", getLocale())
        );

        // Ventajas del sistema
        H2 ventajasTitle = new H2(i18nProvider.getTranslation("welcome.advantages.title", getLocale()));
        Paragraph ventajas = new Paragraph(
                i18nProvider.getTranslation("welcome.advantages.description", getLocale())
        );

        // Cómo funciona (proceso en pasos)
        H2 pasosTitle = new H2(i18nProvider.getTranslation("welcome.steps.title", getLocale()));
        HorizontalLayout pasos = new HorizontalLayout();
        pasos.setWidthFull();
        pasos.setSpacing(true);

        pasos.add(createStep(i18nProvider.getTranslation("welcome.step1", getLocale())));
        pasos.add(createStep(i18nProvider.getTranslation("welcome.step2", getLocale())));
        pasos.add(createStep(i18nProvider.getTranslation("welcome.step3", getLocale())));
        pasos.add(createStep(i18nProvider.getTranslation("welcome.step4", getLocale())));

        // Layout principal
        VerticalLayout content = new VerticalLayout();
        content.setWidth("800px");
        content.setAlignItems(Alignment.CENTER);
        content.add(logo, title, description, ventajasTitle, ventajas, pasosTitle, pasos);

        add(content);
        getStyle().set("text-align", "center");
    }

    private VerticalLayout createStep(String text) {
        Div icon = new Div();
        icon.getStyle()
                .set("width", "50px")
                .set("height", "50px")
                .set("background-color", "#f57c00")
                .set("border-radius", "50%")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        icon.add(new Paragraph("📌"));

        Paragraph stepText = new Paragraph(text);
        stepText.getStyle().set("margin", "0").set("font-weight", "bold");

        VerticalLayout step = new VerticalLayout(icon, stepText);
        step.setAlignItems(Alignment.CENTER);
        step.setSpacing(false);

        return step;
    }
}
