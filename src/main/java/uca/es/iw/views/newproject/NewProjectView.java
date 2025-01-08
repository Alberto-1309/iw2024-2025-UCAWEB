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


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@PageTitle("Solicitar nuevo proyecto")
@Route("new-proyect-form")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("USER")
public class NewProjectView extends Composite<VerticalLayout> {

    private final ProyectoService proyectoService;
    private byte[] memoriaData;
    private byte[] especificacionesData;
    private byte[] presupuestoData;



    public NewProjectView(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;

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
        titulo.setLabel("Título del proyecto: *");
        titulo.setWidth("600px");
        nombrecorto.setLabel("Nombre corto: *");
        nombrecorto.setWidth("600px");
        textSmall11.setText(
                "Memoria del proyecto: *");
        textSmall11.setWidth("100%");
        textSmall11.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h2.setText("Información del solicitante");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");
        nombresolicitante.setLabel("Nombre del solicitante: *");
        nombresolicitante.setWidth("600px");
        textMedium.setText("Tendrá la condición de solicitante el responsable de cualquier área, unidad o centro.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        correo.setLabel("Correo electrónico del solicitante: *");
        correo.setWidth("600px");
        unidad.setLabel("Unidad del solicitante: *");
        unidad.setWidth("600px");
        h22.setText("Información del promotor");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h22);
        h22.setWidth("max-content");
        select.setLabel("Promotor: *");
        select.setWidth("300px");
        proyectoService.setSelectSponsors(select);
        importancia.setLabel("Importancia para el promotor (0-5): *");
        importancia.setWidth("300px");
        h23.setText("Justificación del proyecto");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h23);
        h23.setWidth("max-content");
        checkboxGroup.setLabel("Alineamiento con los objetivos estratégicos: *");
        checkboxGroup.setWidth("600px");
        checkboxGroup.setItems("Innovar, rediseñar y atualizar nuestra oferta formativa para adaptarla a las necesidades sociales y económicas de nuestro entorno.",
                "Conseguir los niveles más altos de calidad en nuestra oferta formativa propia y reglada.",
                "Aumentar significativamente nuestro posicidonamiento en investigación y transferir de forma relevante y útil nuestra investigación a nuestro tejido social y productivo.",
                "Consolidar un modelo de gobierno sostenible y socialmente responsable.",
                "Conseguir que la transparencia sea un valor distintivo y relevante en la UCA.",
                "Generar valor compartido con la Comunidad Universitaria.",
                "Reforzar la importancia del papel de la UCA en la sociedad.");
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);


        textSmall.setText(
                "Su solicitud debe estar alineada con, al menos, uno de los anteriores objetivos estratégicos.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        alcance.setLabel("Alcance: *");
        alcance.setWidth("600px");
        textSmall2.setText(
                "Total de personas de las diferentes áreas, unidades, centros, departamentos o campus que se beneficiarán de la implantación del proyecto");
        textSmall2.setWidth("100%");
        textSmall2.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        fechaObjetivo.setLabel("Fecha requerida de puesta en marcha de la solución TI:");
        fechaObjetivo.setWidth("380px");
        textSmall3.setText(
                "Solo rellenar la fecha límite para la puesta en marcha en el caso de que su motivación sea por obligado cumplimiento de normativa.");
        textSmall3.setWidth("380px");
        textSmall3.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        normativa.setLabel("Normativa de aplicación:");
        normativa.setWidth("600px");
        textSmall4.setText(
                "Solo rellenar la normativa de aplicación en el caso de que su motivación sea por obligado cumplimiento de normativa.");
        textSmall4.setWidth("100%");
        textSmall4.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h24.setText("Información de los interesados");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h24);
        h24.setWidth("max-content");
        interesados.setLabel("Interesados: *");
        interesados.setWidth("600px");
        financiacion.setLabel("Financiación: *");
        financiacion.setWidth("250px");
        textSmall10.setText(
                "Financiación que puede ser aportada por los interesados de cara a la ejecución del proyecto.");
        textSmall10.setWidth("100%");
        textSmall10.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        textSmall5.setText("Especificaciones técnicas: ");
        textSmall5.setWidth("100%");
        textSmall5.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        textSmall6.setText("Presupuesto(s): ");
        textSmall6.setWidth("100%");
        textSmall6.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        h25.setText("Documentación adicional");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h25);
        h25.setWidth("max-content");
        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        saveButton.setText("Enviar");
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
            Notification.show("Proyecto guardado con éxito.", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("");

        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            System.err.println("Detalles del error: " + e.getCause());
            e.printStackTrace();
            Notification.show("Ocurrió un error al crear el proyecto." + e.getMessage(), 7000, Notification.Position.MIDDLE);
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
                Notification.show("Cargado correctamente.", 3000, Notification.Position.MIDDLE);
            } catch (IOException e) {
                Notification.show("Error al cargar ", 5000, Notification.Position.MIDDLE);
            }
        });

        upload.addFileRemovedListener(event -> {
            dataSetter.accept(null); // Limpia los datos almacenados
            Notification.show("Eliminado.", 3000, Notification.Position.MIDDLE);
        });

        return upload;
    }
}
