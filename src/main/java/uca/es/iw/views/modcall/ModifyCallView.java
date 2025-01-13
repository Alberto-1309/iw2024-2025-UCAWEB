package uca.es.iw.views.modcall;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.data.Convocatoria;
import uca.es.iw.data.Recursos;
import uca.es.iw.services.ConvocatoriaService;
import uca.es.iw.services.RecursosService;
import uca.es.iw.views.MainLayout;

@Route(value = "modify-call", layout = MainLayout.class)
@RolesAllowed("CIO")
public class ModifyCallView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ConvocatoriaService convocatoriaService;
    private final RecursosService recursosService;
    private final I18NProvider i18nProvider;

    private final TextField nombreField = new TextField();
    private final TextField objetivoField = new TextField();
    private final DatePicker fechaInicioField = new DatePicker();
    private final DatePicker fechaFinField = new DatePicker();
    private final NumberField presupuestoField = new NumberField();
    private final NumberField recursosHumanosField = new NumberField();

    private Long convocatoriaId;

    @Autowired
    public ModifyCallView(ConvocatoriaService convocatoriaService, RecursosService recursosService, I18NProvider i18nProvider) {
        this.convocatoriaService = convocatoriaService;
        this.recursosService = recursosService;
        this.i18nProvider = i18nProvider;
        // Establecer el título de la página, que luego se pasará al MainLayout
        getUI().ifPresent(ui -> ui.getPage().setTitle(i18nProvider.getTranslation("modify_call.title", getLocale())));

        setWidth("100%");
        setAlignItems(Alignment.CENTER);

        // Diseño de campos
        nombreField.setWidth("100%");
        objetivoField.setWidth("100%");
        fechaInicioField.setWidth("100%");
        fechaFinField.setWidth("100%");
        presupuestoField.setWidth("100%");
        recursosHumanosField.setWidth("100%");

        // Título traducido
        add(new H3(i18nProvider.getTranslation("modify_call.title", getLocale())));

        // Configurar etiquetas traducidas para los campos
        nombreField.setLabel(i18nProvider.getTranslation("modify_call.name", getLocale()));
        objetivoField.setLabel(i18nProvider.getTranslation("modify_call.objective", getLocale()));
        fechaInicioField.setLabel(i18nProvider.getTranslation("modify_call.start_date", getLocale()));
        fechaFinField.setLabel(i18nProvider.getTranslation("modify_call.end_date", getLocale()));
        presupuestoField.setLabel(i18nProvider.getTranslation("modify_call.budget", getLocale()));
        recursosHumanosField.setLabel(i18nProvider.getTranslation("modify_call.human_resources", getLocale()));

        // Configurar botón de guardar con traducción
        Button saveButton = new Button(
                i18nProvider.getTranslation("modify_call.save", getLocale()),
                event -> saveChanges()
        );

        add(nombreField, objetivoField, fechaInicioField, fechaFinField, presupuestoField, recursosHumanosField, saveButton);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if (parameter != null) {
            this.convocatoriaId = parameter;
            loadCall(parameter);
        } else {
            Notification.show(i18nProvider.getTranslation("modify_call.missing_id", getLocale()), 3000, Notification.Position.MIDDLE);
        }
    }

    private void loadCall(Long id) {
        Convocatoria convocatoria = convocatoriaService.getConvocatoriaById(id);
        Recursos recursos = recursosService.getRecursosByConvocatoriaId(convocatoria.getId());
        if (convocatoria != null) {
            nombreField.setValue(convocatoria.getNombre());
            objetivoField.setValue(convocatoria.getObjetivo());
            fechaInicioField.setValue(convocatoria.getFechaApertura());
            fechaFinField.setValue(convocatoria.getFechaCierre());
            presupuestoField.setValue(recursos.getPresupuestoTotal());
            recursosHumanosField.setValue(recursos.getRecursosHumanos().doubleValue());
        } else {
            Notification.show(i18nProvider.getTranslation("modify_call.not_found", getLocale()), 3000, Notification.Position.MIDDLE);
        }
    }

    private void saveChanges() {
        try {
            convocatoriaService.updateConvocatoria(
                    convocatoriaId,
                    nombreField.getValue(),
                    objetivoField.getValue(),
                    fechaInicioField.getValue(),
                    fechaFinField.getValue(),
                    presupuestoField.getValue(),
                    recursosHumanosField.getValue().intValue()
            );
            Notification.show(i18nProvider.getTranslation("modify_call.success", getLocale()), 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("convocatoria-management");
        } catch (Exception e) {
            Notification.show(i18nProvider.getTranslation("modify_call.error", getLocale(), e.getMessage()), 5000, Notification.Position.MIDDLE);
        }
    }
}
