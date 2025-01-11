package uca.es.iw.views.newproject;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import uca.es.iw.data.Convocatoria;
import uca.es.iw.services.ProyectoService;
import com.vaadin.flow.i18n.I18NProvider;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Route(value = "new-proyect-form", layout = uca.es.iw.views.MainLayout.class)
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("USER")
public class NewProjectView extends Composite<VerticalLayout> {

    private final ProyectoService proyectoService;
    private byte[] memoriaData;
    private byte[] especificacionesData;
    private byte[] presupuestoData;
    private final I18NProvider i18nProvider;


    public NewProjectView(ProyectoService proyectoService,  I18NProvider i18nProvider) {
        this.proyectoService = proyectoService;
        this.i18nProvider = i18nProvider;
        VerticalLayout layoutColumn2 = new VerticalLayout();
        TextField titulo = new TextField();
        TextField nombrecorto = new TextField();
        Upload memoria = getUpload(data -> memoriaData = data);
        Paragraph textSmall11 = new Paragraph();
        Hr hr = new Hr();
        H2 h2 = new H2();
        Hr hr2 = new Hr();
        TextField nombresolicitante = new TextField();
        Paragraph textMedium = new Paragraph();
        TextField correo = new TextField();
        TextField unidad = new TextField();
        Hr hr3 = new Hr();
        H2 h22 = new H2();
        Hr hr4 = new Hr();
        ComboBox<String> select = new ComboBox<>();
        NumberField importancia = new NumberField();
        Hr hr5 = new Hr();
        H2 h23 = new H2();
        Hr hr6 = new Hr();
        TextField interesados = new TextField();
        NumberField financiacion = new NumberField();
        Paragraph textSmall10 = new Paragraph();
        Hr hr7 = new Hr();
        H2 h24 = new H2();
        Hr hr8 = new Hr();
        CheckboxGroup checkboxGroup = new CheckboxGroup();
        Paragraph textSmall = new Paragraph();
        TextField alcance = new TextField();
        Paragraph textSmall2 = new Paragraph();
        DatePicker fechaObjetivo = new DatePicker();
        Paragraph textSmall3 = new Paragraph();
        TextField normativa = new TextField();
        Paragraph textSmall4 = new Paragraph();
        Hr hr9 = new Hr();
        H2 h25 = new H2();
        Hr hr10 = new Hr();
        Paragraph textSmall5 = new Paragraph();
        Upload especificaciones = getUpload(data -> especificacionesData = data);
        Paragraph textSmall6 = new Paragraph();
        Upload presupuesto = getUpload(data -> presupuestoData = data);
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button saveButton = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        titulo.setLabel(i18nProvider.getTranslation("new_project.title", getLocale()));
        titulo.setWidth("600px");
        nombrecorto.setLabel(i18nProvider.getTranslation("new_project.short_name", getLocale()));
        nombrecorto.setWidth("600px");
        textSmall11.setText(i18nProvider.getTranslation("new_project.project_memory", getLocale()));
        textSmall11.setWidth("100%");
        textSmall11.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h2.setText(i18nProvider.getTranslation("new_project.requester_info", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");
        nombresolicitante.setLabel(i18nProvider.getTranslation("new_project.requester_name", getLocale()));
        nombresolicitante.setWidth("600px");
        textMedium.setText(i18nProvider.getTranslation("new_project.requester_info_description", getLocale()));
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        correo.setLabel(i18nProvider.getTranslation("new_project.requester_email", getLocale()));
        correo.setWidth("600px");
        unidad.setLabel(i18nProvider.getTranslation("new_project.requester_unit", getLocale()));
        unidad.setWidth("600px");
        h22.setText(i18nProvider.getTranslation("new_project.promoter_info", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h22);
        h22.setWidth("max-content");
        select.setLabel(i18nProvider.getTranslation("new_project.promoter", getLocale()));
        select.setWidth("300px");
        proyectoService.setSelectSponsors(select);
        importancia.setLabel(i18nProvider.getTranslation("new_project.promoter_importance", getLocale()));
        importancia.setWidth("300px");
        h23.setText(i18nProvider.getTranslation("new_project.project_justification", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h23);
        h23.setWidth("max-content");
        checkboxGroup.setLabel(i18nProvider.getTranslation("new_project.alignment_with_strategic_goals", getLocale()));
        checkboxGroup.setWidth("600px");
        checkboxGroup.setItems(i18nProvider.getTranslation("new_project.checkbox_item_1", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_2", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_3", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_4", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_5", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_6", getLocale()),
                i18nProvider.getTranslation("new_project.checkbox_item_7", getLocale()));
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);


        textSmall.setText(i18nProvider.getTranslation("new_project.alignment_explanation", getLocale()));
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        alcance.setLabel(i18nProvider.getTranslation("new_project.scope", getLocale()));
        alcance.setWidth("600px");
        textSmall2.setText(i18nProvider.getTranslation("new_project.scope_description", getLocale()));
        textSmall2.setWidth("100%");
        textSmall2.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        fechaObjetivo.setLabel(i18nProvider.getTranslation("new_project.target_date", getLocale()));
        fechaObjetivo.setWidth("380px");
        textSmall3.setText(i18nProvider.getTranslation("new_project.target_date_description", getLocale()));
        textSmall3.setWidth("380px");
        textSmall3.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        normativa.setLabel(i18nProvider.getTranslation("new_project.applicable_regulations", getLocale()));
        normativa.setWidth("600px");
        textSmall4.setText(i18nProvider.getTranslation("new_project.regulations_description", getLocale()));
        textSmall4.setWidth("100%");
        textSmall4.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h24.setText(i18nProvider.getTranslation("new_project.interested_parties_info", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h24);
        h24.setWidth("max-content");
        interesados.setLabel(i18nProvider.getTranslation("new_project.interested_parties", getLocale()));
        interesados.setWidth("600px");
        financiacion.setLabel(i18nProvider.getTranslation("new_project.financing", getLocale()));
        financiacion.setWidth("250px");
        textSmall10.setText(i18nProvider.getTranslation("new_project.financing_description", getLocale()));
        textSmall10.setWidth("100%");
        textSmall10.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        textSmall5.setText(i18nProvider.getTranslation("new_project.technical_specifications", getLocale()));
        textSmall5.setWidth("100%");
        textSmall5.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        textSmall6.setText(i18nProvider.getTranslation("new_project.budget", getLocale()));
        textSmall6.setWidth("100%");
        textSmall6.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h25.setText(i18nProvider.getTranslation("new_project.additional_documents", getLocale()));
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h25);
        h25.setWidth("max-content");
        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        saveButton.setText(i18nProvider.getTranslation("new_project.submit", getLocale()));
        saveButton.setWidth("min-content");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(event -> {
            //ProyectoService.SampleItem selectedItem = (ProyectoService.SampleItem) select.getValue(); // Realiza el casting aquí
            saveProject(
                    titulo.getValue(),
                    nombrecorto.getValue(),
                    nombresolicitante.getValue(),
                    correo.getValue(),
                    unidad.getValue(),
                    select.getValue(),
                    importancia.getValue().intValue(),
                    interesados.getValue(),
                    financiacion.getValue(),
                    alcance.getValue(),
                    fechaObjetivo.getValue(),
                    normativa.getValue(),
                    checkboxGroup.getValue()
            );
        });

        getContent().add(layoutColumn2);
        layoutColumn2.add(titulo);
        layoutColumn2.add(nombrecorto);
        layoutColumn2.add(textSmall11);
        layoutColumn2.add(memoria);
        layoutColumn2.add(hr);
        layoutColumn2.add(h2);
        layoutColumn2.add(hr2);
        layoutColumn2.add(nombresolicitante);
        layoutColumn2.add(textMedium);
        layoutColumn2.add(correo);
        layoutColumn2.add(unidad);
        layoutColumn2.add(hr3);
        layoutColumn2.add(h22);
        layoutColumn2.add(hr4);
        layoutColumn2.add(select);
        layoutColumn2.add(importancia);
        layoutColumn2.add(hr9);
        layoutColumn2.add(h24);
        layoutColumn2.add(hr10);
        layoutColumn2.add(interesados);
        layoutColumn2.add(financiacion);
        layoutColumn2.add(textSmall10);
        layoutColumn2.add(hr5);
        layoutColumn2.add(h23);
        layoutColumn2.add(hr6);
        layoutColumn2.add(checkboxGroup);
        layoutColumn2.add(textSmall);
        layoutColumn2.add(alcance);
        layoutColumn2.add(textSmall2);
        layoutColumn2.add(fechaObjetivo);
        layoutColumn2.add(textSmall3);
        layoutColumn2.add(normativa);
        layoutColumn2.add(textSmall4);
        layoutColumn2.add(hr7);
        layoutColumn2.add(h25);
        layoutColumn2.add(hr8);
        layoutColumn2.add(textSmall5);
        layoutColumn2.add(especificaciones);
        layoutColumn2.add(textSmall6);
        layoutColumn2.add(presupuesto);
        layoutColumn2.add(layoutRow);
        layoutRow.add(saveButton);


    }

    private void saveProject(String titulo, String nombrecorto, String nombresolicitante, String correo, String unidad, String select, int importancia, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, Object checkboxGroup) {

        List<String> checkboxGroupList = new ArrayList<>(new ArrayList<>((Set<String>) checkboxGroup));

        try {
            // Obtén la convocatoria actual
            Convocatoria convocatoriaActual = proyectoService.getConvocatoriaActual();

            proyectoService.guardarProyecto(
                    titulo,
                    nombrecorto,
                    memoriaData,
                    nombresolicitante,
                    correo,
                    unidad,
                    select,
                    importancia,
                    interesados,
                    financiacion,
                    alcance,
                    fechaObjetivo,
                    normativa,
                    checkboxGroupList,
                    especificacionesData,
                    presupuestoData,
                    convocatoriaActual
            );
            Notification.show(i18nProvider.getTranslation("new_project.project_saved_success", getLocale()), 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("");

        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            System.err.println("Detalles del error: " + e.getCause());
            e.printStackTrace();
            Notification.show(i18nProvider.getTranslation("new_project.project_creation_error", getLocale()) + e.getMessage(), 7000, Notification.Position.MIDDLE);
        }
    }


    private Upload getUpload(Consumer<byte[]> dataSetter) {
        MemoryBuffer buffer = new MemoryBuffer(); // Buffer para almacenar el archivo
        Upload upload = new Upload(buffer);

        upload.setAcceptedFileTypes("application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/rtf", "text/markdown", "text/csv",
                "application/xml", "application/vnd.oasis.opendocument.text"); // Tipos aceptados
        upload.setMaxFileSize(16 * 1024 * 1024); // Tamaño máximo: 16MB

        upload.addSucceededListener(event -> {
            try {
                byte[] fileData = buffer.getInputStream().readAllBytes(); // Leer los datos del archivo
                dataSetter.accept(fileData); // Guardar los datos en la variable correspondiente
                Notification.show(i18nProvider.getTranslation("new_project.upload_success", getLocale()), 3000, Notification.Position.MIDDLE);
            } catch (IOException e) {
                Notification.show(i18nProvider.getTranslation("new_project.upload_error", getLocale()), 5000, Notification.Position.MIDDLE);
            }
        });

        upload.addFileRemovedListener(event -> {
            dataSetter.accept(null); // Limpia los datos almacenados
            Notification.show(i18nProvider.getTranslation("new_project.file_removed", getLocale()), 3000, Notification.Position.MIDDLE);
        });

        return upload;
    }
}
