package uca.es.iw.views.managecall;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.data.Convocatoria;
import uca.es.iw.data.Recursos;
import uca.es.iw.services.ConvocatoriaService;
import uca.es.iw.services.RecursosService;
import uca.es.iw.views.MainLayout;
import uca.es.iw.views.modcall.ModifyCallView;

import java.util.List;

@Route(value = "convocatoria-management", layout = MainLayout.class)
@Menu(order = 11, icon = "line-awesome/svg/calendar.svg")
@RolesAllowed("CIO")
public class CallView extends VerticalLayout {

    private final ConvocatoriaService convocatoriaService;
    private final RecursosService recursosService;
    private final I18NProvider i18nProvider;

    private final Grid<Convocatoria> convocatoriaGrid = new Grid<>(Convocatoria.class, false);
    private final TextField nombreField = new TextField();
    private final TextField objetivoField = new TextField();
    private final DatePicker fechaInicioField = new DatePicker();
    private final DatePicker fechaFinField = new DatePicker();
    private final NumberField presupuestoField = new NumberField();
    private final NumberField recursosHumanosField = new NumberField();
    private final Button saveButton;
    private final Button clearButton;

    private Long editingId = null;

    @Autowired
    public CallView(ConvocatoriaService convocatoriaService, RecursosService recursosService, I18NProvider i18nProvider) {
        this.convocatoriaService = convocatoriaService;
        this.recursosService = recursosService;
        this.i18nProvider = i18nProvider;

        // Establecer título de la página traducido
        getUI().ifPresent(ui -> ui.getPage().setTitle(i18nProvider.getTranslation("call.title", getLocale())));

        // Configuración de diseño
        setWidth("100%");
        setAlignItems(FlexComponent.Alignment.CENTER);

        // Título
        H3 title = new H3(i18nProvider.getTranslation("call.title", getLocale()));
        add(title);

        // Campos del formulario
        nombreField.setWidth("100%");
        objetivoField.setWidth("100%");
        fechaInicioField.setWidth("100%");
        fechaFinField.setWidth("100%");
        presupuestoField.setWidth("100%");
        recursosHumanosField.setWidth("100%");

        // Configurar campos del formulario con traducción
        nombreField.setLabel(i18nProvider.getTranslation("call.name", getLocale()));
        objetivoField.setLabel(i18nProvider.getTranslation("call.objective", getLocale()));
        fechaInicioField.setLabel(i18nProvider.getTranslation("call.start_date", getLocale()));
        fechaFinField.setLabel(i18nProvider.getTranslation("call.end_date", getLocale()));
        presupuestoField.setLabel(i18nProvider.getTranslation("call.budget", getLocale()));
        recursosHumanosField.setLabel(i18nProvider.getTranslation("call.human_resources", getLocale()));

        // Configuración de botones con traducción
        saveButton = new Button(i18nProvider.getTranslation("call.save", getLocale()), event -> saveConvocatoria());
        clearButton = new Button(i18nProvider.getTranslation("call.clear", getLocale()), event -> clearForm());

        // Diseño de botones
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, clearButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Diseño del formulario
        add(nombreField, objetivoField, fechaInicioField, fechaFinField, presupuestoField, recursosHumanosField, buttonsLayout);

        // Configuración del grid
        configureGrid();
        convocatoriaGrid.setWidthFull();
        add(convocatoriaGrid);

        // Cargar convocatorias
        loadCalls();
    }

    private void configureGrid() {
        convocatoriaGrid.addColumn(Convocatoria::getNombre)
                .setHeader(i18nProvider.getTranslation("call.grid.name", getLocale()))
                .setSortable(true);
        convocatoriaGrid.addColumn(Convocatoria::getObjetivo)
                .setHeader(i18nProvider.getTranslation("call.grid.objective", getLocale()))
                .setSortable(true);
        convocatoriaGrid.addColumn(Convocatoria::getFechaApertura)
                .setHeader(i18nProvider.getTranslation("call.grid.start_date", getLocale()))
                .setSortable(true);
        convocatoriaGrid.addColumn(Convocatoria::getFechaCierre)
                .setHeader(i18nProvider.getTranslation("call.grid.end_date", getLocale()))
                .setSortable(true);

        // Mostrar presupuesto y recursos humanos de la tabla Recursos asociada
        convocatoriaGrid.addColumn(convocatoria -> {
                    try {
                        Recursos recurso = recursosService.getRecursosByConvocatoriaId(convocatoria.getId());
                        if (recurso != null) {
                            return String.format("%,.2f", recurso.getPresupuestoTotal());
                        } else {
                            return "0.00"; // Si no hay recursos asociados, muestra 0
                        }
                    } catch (Exception e) {
                        return "Error"; // Muestra "Error" si ocurre una excepción
                    }
                }).setHeader(i18nProvider.getTranslation("call.grid.budget", getLocale()))
                .setSortable(true);


        convocatoriaGrid.addColumn(convocatoria -> {
                    // Obtener los recursos humanos del recurso asociado a la convocatoria (solo un recurso)
                    Recursos recurso = recursosService.getRecursosByConvocatoriaId(convocatoria.getId());
                    if (recurso != null) {
                        return String.format("%d", recurso.getRecursosHumanos());
                    } else {
                        return "0.00";  // Si no hay recursos asociados, muestra 0
                    }
                }).setHeader(i18nProvider.getTranslation("call.grid.human_resources", getLocale()))
                .setSortable(true);

        convocatoriaGrid.addComponentColumn(convocatoria -> {
            Button editButton = new Button(i18nProvider.getTranslation("call.grid.edit", getLocale()), event ->
                    UI.getCurrent().navigate(ModifyCallView.class, convocatoria.getId()));
            Button deleteButton = new Button(i18nProvider.getTranslation("call.grid.delete", getLocale()), event ->
                    deleteConvocatoria(convocatoria));
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(i18nProvider.getTranslation("call.grid.actions", getLocale()));
    }

    private void loadCalls() {
        List<Convocatoria> convocatorias = convocatoriaService.getAllConvocatorias();
        if (convocatorias != null) {
            convocatoriaGrid.setItems(convocatorias);
        } else {
            Notification.show(i18nProvider.getTranslation("call.notification.no_calls", getLocale()));
        }
    }

    private void saveConvocatoria() {
        try {
            if (editingId == null) {
                convocatoriaService.createConvocatoria(
                        nombreField.getValue(),
                        objetivoField.getValue(),
                        fechaInicioField.getValue(),
                        fechaFinField.getValue(),
                        presupuestoField.getValue(),
                        recursosHumanosField.getValue().intValue()
                );
                Notification.show(i18nProvider.getTranslation("call.notification.created", getLocale()));
            } else {
                convocatoriaService.updateConvocatoria(
                        editingId,
                        nombreField.getValue(),
                        objetivoField.getValue(),
                        fechaInicioField.getValue(),
                        fechaFinField.getValue(),
                        presupuestoField.getValue(),
                        recursosHumanosField.getValue().intValue()
                );
                Notification.show(i18nProvider.getTranslation("call.notification.updated", getLocale()));
                editingId = null;
            }
            clearForm();
            loadCalls();
            // Recargar la página para actualizar la tabla
            UI.getCurrent().getPage().reload();
        } catch (Exception e) {
            Notification.show(i18nProvider.getTranslation("call.notification.error", getLocale(), e.getMessage()), 5000, Notification.Position.MIDDLE);
        }
    }

    private void deleteConvocatoria(Convocatoria convocatoria) {
        try {
            convocatoriaService.deleteConvocatoria(convocatoria.getId());
            Notification.show(i18nProvider.getTranslation("call.notification.deleted", getLocale()));
            loadCalls();
        } catch (Exception e) {
            Notification.show(i18nProvider.getTranslation("call.notification.error", getLocale(), e.getMessage()), 5000, Notification.Position.MIDDLE);
        }
    }

    private void clearForm() {
        editingId = null;
        nombreField.clear();
        objetivoField.clear();
        fechaInicioField.clear();
        fechaFinField.clear();
        presupuestoField.clear();
        recursosHumanosField.clear();
    }
}
