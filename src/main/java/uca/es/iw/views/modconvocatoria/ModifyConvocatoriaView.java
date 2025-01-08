package uca.es.iw.views.modconvocatoria;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.data.Convocatoria;
import uca.es.iw.services.ConvocatoriaService;
import uca.es.iw.views.MainLayout;

@Route(value = "modificar-convocatoria", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ModifyConvocatoriaView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ConvocatoriaService convocatoriaService;

    private final TextField nombreField = new TextField("Nombre de la convocatoria");
    private final TextField objetivoField = new TextField("Objetivo");
    private final DatePicker fechaInicioField = new DatePicker("Fecha de inicio");
    private final DatePicker fechaFinField = new DatePicker("Fecha de fin");
    private final NumberField presupuestoField = new NumberField("Presupuesto (€)");
    private final NumberField recursosHumanosField = new NumberField("Recursos Humanos");

    private Long convocatoriaId;

    @Autowired
    public ModifyConvocatoriaView(ConvocatoriaService convocatoriaService) {
        this.convocatoriaService = convocatoriaService;

        setWidth("100%");
        setAlignItems(Alignment.CENTER);

        // Diseño
        add(new H3("Modificar Convocatoria"));

        nombreField.setWidth("100%");
        objetivoField.setWidth("100%");
        fechaInicioField.setWidth("100%");
        fechaFinField.setWidth("100%");
        presupuestoField.setWidth("100%");
        recursosHumanosField.setWidth("100%");

        Button saveButton = new Button("Guardar cambios", event -> saveChanges());
        add(nombreField, objetivoField, fechaInicioField, fechaFinField, presupuestoField, recursosHumanosField, saveButton);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if (parameter != null) {
            this.convocatoriaId = parameter;
            loadConvocatoria(parameter);
        } else {
            Notification.show("ID de convocatoria no proporcionado.", 3000, Notification.Position.MIDDLE);
        }
    }

    private void loadConvocatoria(Long id) {
        Convocatoria convocatoria = convocatoriaService.getConvocatoriaById(id);
        if (convocatoria != null) {
            nombreField.setValue(convocatoria.getNombre());
            objetivoField.setValue(convocatoria.getObjetivo());
            fechaInicioField.setValue(convocatoria.getFechaApertura());
            fechaFinField.setValue(convocatoria.getFechaCierre());
            presupuestoField.setValue(convocatoria.getPresupuestoTotal());
            recursosHumanosField.setValue((double) convocatoria.getCupoRecursosHumanos());
        } else {
            Notification.show("Convocatoria no encontrada.", 3000, Notification.Position.MIDDLE);
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
            Notification.show("Convocatoria actualizada con éxito.", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("convocatoria-management");
        } catch (Exception e) {
            Notification.show("Error al guardar los cambios: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}