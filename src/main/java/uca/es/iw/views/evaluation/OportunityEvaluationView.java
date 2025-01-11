package uca.es.iw.views.evaluation;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import uca.es.iw.services.ProyectoService;
import com.vaadin.flow.i18n.I18NProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Route(value = "oportunityevaluation", layout = uca.es.iw.views.MainLayout.class)
@Menu(order = 8, icon = "line-awesome/svg/clipboard-list-solid.svg")
@RolesAllowed("CIO")
public class OportunityEvaluationView extends Composite<VerticalLayout> {
    private final I18NProvider i18nProvider;
    @ClientCallable
    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    ProyectoService proyectoService;

    public OportunityEvaluationView(ProyectoService proyectoService, I18NProvider i18nProvider) {
        this.proyectoService = proyectoService;
        this.i18nProvider = i18nProvider;

        VerticalLayout layoutColumn2 = new VerticalLayout();
        FormLayout formLayout2Col = new FormLayout();
        ComboBox<String> comboBox = new ComboBox<>();
        Hr hr = new Hr();
        H2 h2 = new H2();
        Hr hr2 = new Hr();
        Hr hr3 = new Hr();
        H2 h22 = new H2();
        Hr hr4 = new Hr();
        Hr hr5 = new Hr();
        H2 h23 = new H2();
        Hr hr6 = new Hr();
        Hr hr7 = new Hr();
        H2 h24 = new H2();
        Hr hr8 = new Hr();
        Hr hr9 = new Hr();
        H2 h25 = new H2();
        Hr hr10 = new Hr();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimary = new Button();

        // Nuevo botón de descarga
        Button downloadButton = new Button(i18nProvider.getTranslation("oportunity.download.memory", getLocale()));
        downloadButton.setEnabled(false); // Inicialmente deshabilitado
        downloadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button downloadButton2 = new Button(i18nProvider.getTranslation("oportunity.download.specifications", getLocale()));
        downloadButton2.setEnabled(false); // Inicialmente deshabilitado
        downloadButton2.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Añadir Span para mostrar la calificación de oportunidad
        Span calificacionOportunidadLabel = new Span(i18nProvider.getTranslation("oportunity.rating.not.qualified", getLocale()));
        calificacionOportunidadLabel.setVisible(false);  // Inicialmente oculto

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        layoutColumn2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");

        formLayout2Col.setWidth("100%");
        comboBox.setLabel(i18nProvider.getTranslation("oportunity.select.project", getLocale()));
        comboBox.setWidth("min-content");

        // Llenar ComboBox con datos de proyectos
        proyectoService.setSelectProjects(comboBox);

        // Evento de selección de ComboBox
        comboBox.addValueChangeListener(event -> {
            String selectedProject = event.getValue();
            downloadButton.setEnabled(selectedProject != null); // Activa o desactiva el botón
            downloadButton2.setEnabled(selectedProject != null); // Activa o desactiva el botón

            if (selectedProject != null) {
                // Mostrar el Span
                calificacionOportunidadLabel.setVisible(true);

                // Obtener la calificación de oportunidad desde el servicio (si existe)
                Double calOportunidad = proyectoService.getCalificacionOportunidad(selectedProject);
                if (calOportunidad != null) {
                    calificacionOportunidadLabel.setText("Calificación de oportunidad: " + calOportunidad);
                } else {
                    calificacionOportunidadLabel.setText("Calificación de oportunidad: Sin calificar");
                }
            } else {
                // Ocultar el Span si no hay proyecto seleccionado
                calificacionOportunidadLabel.setVisible(false);
            }
        });

        downloadButton.addClickListener(e -> {
            String selectedProject = comboBox.getValue(); // Obtiene el proyecto seleccionado actual
            if (selectedProject != null) {
                String downloadUrl = proyectoService.getDownloadUrl(selectedProject, 1);
                // Validar si el archivo existe antes de abrir la pestaña
                UI.getCurrent().getPage().executeJs(
                        "fetch($0, { method: 'HEAD' }).then(response => { " +
                                "if (response.ok) {" +
                                "    window.open($0, '_blank');" +
                                "} else {" +
                                "    $1.$server.showNotification('La memoria no está disponible para el proyecto seleccionado.');" +
                                "}});",
                        downloadUrl, getElement());
            }
        });

        downloadButton2.addClickListener(e -> {
            String selectedProject = comboBox.getValue(); // Obtiene el proyecto seleccionado actual
            if (selectedProject != null) {
                String downloadUrl = proyectoService.getDownloadUrl(selectedProject, 2);
                // Validar si el archivo existe antes de abrir la pestaña
                UI.getCurrent().getPage().executeJs(
                        "fetch($0, { method: 'HEAD' }).then(response => { " +
                                "if (response.ok) {" +
                                "    window.open($0, '_blank');" +
                                "} else {" +
                                "    $1.$server.showNotification('Las especificaciones no están disponibles para el proyecto seleccionado.');" +
                                "}});",
                        downloadUrl, getElement());
            }
        });

        h2.setText(i18nProvider.getTranslation("oportunity.need.and.urgency", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");

        // Añadir campos de evaluación para "Necesidad y urgencia"
        ComboBox<Integer> necesidadImportancia = new ComboBox<>(i18nProvider.getTranslation("oportunity.need.importance", getLocale()));
        necesidadImportancia.setItems(getNumericOptions());
        necesidadImportancia.setWidth("400px");
        ComboBox<Integer> necesidadUrgencia = new ComboBox<>(i18nProvider.getTranslation("oportunity.need.urgency", getLocale()));
        necesidadUrgencia.setItems(getNumericOptions());
        necesidadUrgencia.setWidth("400px");

        h22.setText(i18nProvider.getTranslation("oportunity.alignment.objectives", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h22);
        h22.setWidth("max-content");

        h23.setText(i18nProvider.getTranslation("oportunity.impact.benefits", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h23);
        h23.setWidth("max-content");

        h24.setText(i18nProvider.getTranslation("oportunity.risks.complexity", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h24);
        h24.setWidth("max-content");

        h25.setText(i18nProvider.getTranslation("oportunity.memory.milestones", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h25);
        h25.setWidth("max-content");

        // Añadir checkbox group para "Alineación con los objetivos estratégicos"
        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel(i18nProvider.getTranslation("oportunity.alignment.objectives.group", getLocale()));
        checkboxGroup.setWidth("600px");
        checkboxGroup.setItems(
                "Innovar, rediseñar y actualizar nuestra oferta formativa para adaptarla a las necesidades sociales y económicas de nuestro entorno.",
                "Conseguir los niveles más altos de calidad en nuestra oferta formativa propia y reglada.",
                "Aumentar significativamente nuestro posicionamiento en investigación y transferir de forma relevante y útil nuestra investigación a nuestro tejido social y productivo.",
                "Consolidar un modelo de gobierno sostenible y socialmente responsable.",
                "Conseguir que la transparencia sea un valor distintivo y relevante en la UCA.",
                "Generar valor compartido con la Comunidad Universitaria.",
                "Reforzar la importancia del papel de la UCA en la sociedad."
        );
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        // Más campos de evaluación...
        // Impacto y beneficios
        ComboBox<String> impactoPersonas = new ComboBox<>(i18nProvider.getTranslation("oportunity.impact.people", getLocale()));
        impactoPersonas.setItems(
                "1 a 50",
                "50 a 500",
                "500 a 2000",
                "2000 a 10000",
                "10000 o más"
        );
        impactoPersonas.setWidth("400px");
        ComboBox<String> ahorroMejoras = new ComboBox<>(i18nProvider.getTranslation("oportunity.savings.quality.improvement", getLocale()));
        ahorroMejoras.setItems(
                "Sin impacto notable",
                "Impacto leve",
                "Impacto moderado",
                "Impacto alto",
                "Impacto excepcional"
        );
        ahorroMejoras.setWidth("400px");

        // Riesgos y complejidad
        ComboBox<String> riesgosComplejidad = new ComboBox<>(i18nProvider.getTranslation("oportunity.risks.complexity", getLocale()));
        riesgosComplejidad.setItems(
                "Sin dificultades",
                "Dificultades leves",
                "Dificultades moderadas",
                "Dificultades altas",
                "Dificultades críticas"
        );
        riesgosComplejidad.setWidth("400px");
        ComboBox<String> dimensionSostenibilidad = new ComboBox<>(i18nProvider.getTranslation("oportunity.dimension.sustainability", getLocale()));
        dimensionSostenibilidad.setItems(
                "Muy bajo",
                "Bajo",
                "Moderado",
                "Alto",
                "Muy alto"
        );
        dimensionSostenibilidad.setWidth("400px");

        // Memoria e hitos
        ComboBox<String> memoriaCompleta = new ComboBox<>(i18nProvider.getTranslation("oportunity.memory.complete", getLocale()));
        memoriaCompleta.setItems(
                "Incompleta",
                "Pácticamente incompleta",
                "Aceptable",
                "Mayormente completa",
                "Completa"
        );
        memoriaCompleta.setWidth("400px");
        ComboBox<String> hitosClaros = new ComboBox<>(i18nProvider.getTranslation("oportunity.clear.milestones", getLocale()));
        hitosClaros.setItems(
                "Nada claros",
                "Incompletos",
                "Moderadamente claros",
                "Bien detallados",
                "Perfectamente detallados"
        );
        hitosClaros.setWidth("400px");

        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");

        buttonPrimary.setText(i18nProvider.getTranslation("oportunity.save", getLocale()));
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary.addClickListener(e -> {
            String selectedProject = comboBox.getValue();

            // Obtener los valores de todas las subcategorías
            Integer importancia = necesidadImportancia.getValue();
            Integer urgencia = necesidadUrgencia.getValue();
            String impacto_ = impactoPersonas.getValue();
            Integer impacto = switch (impacto_) {
                case "1 a 50" -> 1;
                case "50 a 500" -> 2;
                case "500 a 2000" -> 3;
                case "2000 a 10000" -> 4;
                case "10000 o más" -> 5;
                default -> null;
            };
            String ahorro_ = ahorroMejoras.getValue();
            Integer ahorro = switch (ahorro_) {
                case "Sin impacto notable" -> 0;
                case "Impacto leve" -> 1;
                case "Impacto moderado" -> 2;
                case "Impacto alto" -> 3;
                case "Impacto excepcional" -> 4;
                default -> null;
            };
            String riesgos_ = riesgosComplejidad.getValue();
            Integer riesgos = switch (riesgos_) {
                case "Sin dificultades" -> 4;
                case "Dificultades leves" -> 3;
                case "Dificultades moderadas" -> 2;
                case "Dificultades altas" -> 1;
                case "Dificultades críticas" -> 0;
                default -> null;
            };
            String dimension_ = dimensionSostenibilidad.getValue();
            Integer dimension = switch (dimension_) {
                case "Muy bajo" -> 1;
                case "Bajo" -> 2;
                case "Moderado" -> 3;
                case "Alto" -> 4;
                case "Muy alto" -> 5;
                default -> null;
            };
            String memoria_ = memoriaCompleta.getValue();
            Integer memoria = switch (memoria_) {
                case "Incompleta" -> 0;
                case "Pácticamente incompleta" -> 1;
                case "Aceptable" -> 2;
                case "Mayormente completa" -> 3;
                case "Completa" -> 4;
                default -> null;
            };
            String hitos_ = hitosClaros.getValue();
            Integer hitos = switch (hitos_) {
                case "Nada claros" -> 1;
                case "Incompletos" -> 2;
                case "Moderadamente claros" -> 3;
                case "Bien detallados" -> 4;
                case "Perfectamente detallados" -> 5;
                default -> 0;
            };
            Set<String> objetivosSeleccionados = checkboxGroup.getSelectedItems();

            // Validar que se hayan seleccionado todos los campos
            if (selectedProject == null) {
                Notification.show(i18nProvider.getTranslation("notification.select.project", getLocale()), 3000, Notification.Position.MIDDLE);
                return;
            }

            if (importancia == null || urgencia == null || impacto == null || ahorro == null || riesgos == null || dimension == null || memoria == null) {
                Notification.show(i18nProvider.getTranslation("notification.complete.all.ratings", getLocale()), 3000, Notification.Position.MIDDLE);
                return;
            }

            // Calcular la calificación de oportunidad
            int calOportunidad_ = (importancia + urgencia + impacto + ahorro + riesgos + dimension + memoria + hitos + (objetivosSeleccionados.size() * 2));

            Double calOportunidad = calOportunidad_ / 5.1;

            try {
                proyectoService.updateCalOportunidad(selectedProject, calOportunidad);
                Notification.show(i18nProvider.getTranslation("notification.oportunity.rating.updated", getLocale()), 3000, Notification.Position.MIDDLE);

                // Limpiar los campos después de la acción
                comboBox.clear();
                impactoPersonas.clear();
                ahorroMejoras.clear();
                necesidadImportancia.clear();
                necesidadUrgencia.clear();
                riesgosComplejidad.clear();
                dimensionSostenibilidad.clear();
                memoriaCompleta.clear();
                hitosClaros.clear();
                checkboxGroup.clear();
                calificacionOportunidadLabel.setVisible(false);
            } catch (Exception ex) {
                Notification.show(i18nProvider.getTranslation("notification.error.saving.data", getLocale()), 3000, Notification.Position.MIDDLE);
            }
        });

        getContent().add(layoutColumn2);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(comboBox);
        layoutColumn2.add(downloadButton);
        layoutColumn2.add(downloadButton2);
        layoutColumn2.add(calificacionOportunidadLabel);
        layoutColumn2.add(hr);
        layoutColumn2.add(h2);
        layoutColumn2.add(hr2);
        layoutColumn2.add(necesidadImportancia);
        layoutColumn2.add(necesidadUrgencia);
        layoutColumn2.add(hr3);
        layoutColumn2.add(h22);
        layoutColumn2.add(hr4);
        layoutColumn2.add(checkboxGroup);
        layoutColumn2.add(hr5);
        layoutColumn2.add(h23);
        layoutColumn2.add(hr6);
        layoutColumn2.add(impactoPersonas);
        layoutColumn2.add(ahorroMejoras);
        layoutColumn2.add(hr7);
        layoutColumn2.add(h24);
        layoutColumn2.add(hr8);
        layoutColumn2.add(riesgosComplejidad);
        layoutColumn2.add(dimensionSostenibilidad);
        layoutColumn2.add(hr9);
        layoutColumn2.add(h25);
        layoutColumn2.add(hr10);
        layoutColumn2.add(memoriaCompleta);
        layoutColumn2.add(hitosClaros);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimary);
    }

    private List<Integer> getNumericOptions() {
        int start = 1;
        int end = 5;
        List<Integer> options = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            options.add(i);
        }
        return options;
    }
}

