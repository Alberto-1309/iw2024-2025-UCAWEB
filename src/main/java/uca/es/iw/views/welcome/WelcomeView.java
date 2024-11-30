package uca.es.iw.views.welcome;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Bienvenido")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/user.svg")
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        // Configuración del layout principal
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Imagen del encabezado
        Image logo = new Image("images/uca-logo.png", "UCA Logo");
        logo.setWidth("150px");

        // Título principal
        H2 title = new H2("Impulsa la Innovación en Proyectos de TI");
        title.getStyle().set("margin-top", "0");

        // Párrafo descriptivo
        Paragraph description = new Paragraph(
                "Facilitamos la presentación, evaluación y seguimiento de proyectos de TI que ayudan a alinear las iniciativas con los objetivos estratégicos de nuestra institución."
        );

        // Ventajas del sistema
        H2 ventajasTitle = new H2("Ventajas de Participar en los Proyectos de TI");
        Paragraph ventajas = new Paragraph(
                "Facilita la colaboración y el seguimiento del progreso de tus iniciativas. " +
                        "Presenta tus ideas, recibe notificaciones de avance, y accede a información en tiempo real."
        );

        // Cómo funciona (proceso en pasos)
        H2 pasosTitle = new H2("¿Cómo Funciona?");
        HorizontalLayout pasos = new HorizontalLayout();
        pasos.setWidthFull();
        pasos.setSpacing(true);

        pasos.add(createStep("Presenta tu propuesta"));
        pasos.add(createStep("Evaluación por parte de expertos"));
        pasos.add(createStep("Seguimiento en línea"));
        pasos.add(createStep("Notificación de estado"));

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
        icon.add(new Paragraph("📌")); // Puedes cambiar esto por un ícono más apropiado

        Paragraph stepText = new Paragraph(text);
        stepText.getStyle().set("margin", "0").set("font-weight", "bold");

        VerticalLayout step = new VerticalLayout(icon, stepText);
        step.setAlignItems(Alignment.CENTER);
        step.setSpacing(false);

        return step;
    }
}
