package uca.es.iw.views.project;

import com.vaadin.flow.component.ClientCallable;
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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import uca.es.iw.data.Proyecto;
import uca.es.iw.security.AuthenticatedUser;
import uca.es.iw.services.ProyectoService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@PageTitle("Vista del Proyecto")
@Route("project-view/:projectId")
@RolesAllowed("USER")
public class ProjectView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    @ClientCallable
    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private final ProyectoService proyectoService;
    private final AuthenticatedUser authenticatedUser;
    private Proyecto proyecto; // Proyecto actual
    private Long currentUserId; // ID del usuario actual

    private boolean isEditable = false; // Indica si los campos son editables

    private byte[] memoriaData;
    private byte[] especificacionesData;
    private byte[] presupuestoData;

    private Long id = null;
    private final TextField titulo = new TextField();
    private final TextField nombrecorto = new TextField();
    private final TextField nombresolicitante = new TextField();
    private final TextField correo = new TextField();
    private final ComboBox<String> select = new ComboBox<>();
    private final NumberField importancia = new NumberField();
    private final TextField interesados = new TextField();
    private final NumberField financiacion = new NumberField();
    private final TextField alcance = new TextField();
    private final DatePicker fechaObjetivo = new DatePicker();
    private final TextField normativa = new TextField();
    private final CheckboxGroup checkboxGroup = new CheckboxGroup();
    private final Upload memoria = getUpload(data -> memoriaData = data);
    private final Upload especificaciones = getUpload(data -> especificacionesData = data);
    private final Upload presupuesto = getUpload(data -> presupuestoData = data);
    private final Button saveButton = new Button();
    private final Span estadoActual = new Span();
    private final Span calificacion = new Span();
    private final Span fechaCreacion = new Span();

    public ProjectView(ProyectoService proyectoService, AuthenticatedUser authenticatedUser) {
        this.proyectoService = proyectoService;

        VerticalLayout layoutColumn2 = new VerticalLayout();
        Paragraph textSmall11 = new Paragraph();
        H2 h2 = new H2();
        Paragraph textMedium = new Paragraph();
        Hr hr3 = new Hr();
        H2 h22 = new H2();
        Hr hr4 = new Hr();
        Hr hr5 = new Hr();
        H2 h23 = new H2();
        Hr hr6 = new Hr();
        Paragraph textSmall10 = new Paragraph();
        Hr hr7 = new Hr();
        H2 h24 = new H2();
        Hr hr8 = new Hr();
        Paragraph textSmall = new Paragraph();
        Paragraph textSmall2 = new Paragraph();
        Paragraph textSmall3 = new Paragraph();
        Paragraph textSmall4 = new Paragraph();
        Hr hr9 = new Hr();
        H2 h25 = new H2();
        Hr hr10 = new Hr();
        Paragraph textSmall5 = new Paragraph();
        Paragraph textSmall6 = new Paragraph();
        HorizontalLayout layoutRow = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
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
        // Botones de descarga
        Button downloadMemoryButton = new Button("Descargar memoria");
        Button downloadSpecsButton = new Button("Descargar especificaciones técnicas");
        Button downloadBudgetButton = new Button("Descargar presupuestos");

        downloadMemoryButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        downloadSpecsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        downloadBudgetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        downloadMemoryButton.addClickListener(e -> {
            String selectedProject = proyecto.getNombreCorto();
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
        });

        downloadSpecsButton.addClickListener(e -> {
            String selectedProject = proyecto.getNombreCorto();
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
        });

        downloadBudgetButton.addClickListener(e -> {
            String selectedProject = proyecto.getNombreCorto();
            String downloadUrl = proyectoService.getDownloadUrl(selectedProject, 3);
            // Validar si el archivo existe antes de abrir la pestaña
            UI.getCurrent().getPage().executeJs(
                    "fetch($0, { method: 'HEAD' }).then(response => { " +
                            "if (response.ok) {" +
                            "    window.open($0, '_blank');" +
                            "} else {" +
                            "    $1.$server.showNotification('Los presupuestos no están disponibles para el proyecto seleccionado.');" +
                            "}});",
                    downloadUrl, getElement());
        });

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
        saveButton.setText("Actualizar proyecto");
        saveButton.setWidth("min-content");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(event -> {
            saveProject(
                    id,
                    titulo.getValue(),
                    nombrecorto.getValue(),
                    select.getValue(),
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
        layoutColumn2.add(downloadMemoryButton);
        layoutColumn2.add(memoria);
        layoutColumn2.add(estadoActual);
        layoutColumn2.add(calificacion);
        layoutColumn2.add(fechaCreacion);
        layoutColumn2.add(hr3);
        layoutColumn2.add(h22);
        layoutColumn2.add(hr4);
        layoutColumn2.add(select);
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
        layoutColumn2.add(downloadSpecsButton);
        layoutColumn2.add(especificaciones);
        layoutColumn2.add(textSmall6);
        layoutColumn2.add(downloadBudgetButton);
        layoutColumn2.add(presupuesto);
        layoutColumn2.add(layoutRow);
        layoutRow.add(saveButton);
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authenticatedUser.get().ifPresent(authenticatedUser -> {
            currentUserId = authenticatedUser.getId();
        });

        String projectId = event.getRouteParameters().get("projectId").orElse(null);

        if (projectId == null) {
            Notification.show("No se proporcionó un ID de proyecto válido.", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate(""); // Redirigir si no hay ID
            return;
        }

        try {
            proyecto = proyectoService.findById(Long.parseLong(projectId));

            if (proyecto == null) {
                Notification.show("No se pudo encontrar el proyecto.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate(""); // Redirigir si no existe el proyecto
                return;
            }

            //System.out.println("currentUserId: " + currentUserId);
            isEditable = proyecto.getCreadoId().equals(currentUserId);

            // Configurar valores en los campos después de cargar el proyecto
            id = proyecto.getId();
            titulo.setValue(proyecto.getTitulo());
            nombrecorto.setValue(proyecto.getNombreCorto());
            select.setValue(proyecto.getPromotor());
            interesados.setValue(proyecto.getInteresados());
            financiacion.setValue(proyecto.getFinanciacion());
            alcance.setValue(proyecto.getAlcance());
            fechaObjetivo.setValue(proyecto.getFechaObjetivo());
            normativa.setValue(proyecto.getNormativa());
            estadoActual.setText("Estado actual: " + proyecto.getEstado());
            if (proyecto.getCalFinal() == null) {
                calificacion.setText("Calificación: No calificado");
            } else
                calificacion.setText("Calificación: " + proyecto.getCalFinal());

            fechaCreacion.setText("Fecha de creación: " + proyecto.getFechaCreado());

            boolean aoe1 = proyecto.getAoe1();
            boolean aoe2 = proyecto.getAoe2();
            boolean aoe3 = proyecto.getAoe3();
            boolean aoe4 = proyecto.getAoe4();
            boolean aoe5 = proyecto.getAoe5();
            boolean aoe6 = proyecto.getAoe6();
            boolean aoe7 = proyecto.getAoe7();

            if (aoe1) {
                checkboxGroup.select("Innovar, rediseñar y atualizar nuestra oferta formativa para adaptarla a las necesidades sociales y económicas de nuestro entorno.");
            }
            if (aoe2) {
                checkboxGroup.select("Conseguir los niveles más altos de calidad en nuestra oferta formativa propia y reglada.");
            }
            if (aoe3) {
                checkboxGroup.select("Aumentar significativamente nuestro posicidonamiento en investigación y transferir de forma relevante y útil nuestra investigación a nuestro tejido social y productivo.");
            }
            if (aoe4) {
                checkboxGroup.select("Consolidar un modelo de gobierno sostenible y socialmente responsable.");
            }
            if (aoe5) {
                checkboxGroup.select("Conseguir que la transparencia sea un valor distintivo y relevante en la UCA.");
            }
            if (aoe6) {
                checkboxGroup.select("Generar valor compartido con la Comunidad Universitaria.");
            }
            if (aoe7) {
                checkboxGroup.select("Reforzar la importancia del papel de la UCA en la sociedad.");
            }

            // Ajustar los campos según la editabilidad
            setFieldsEditable(isEditable);

        } catch (Exception e) {
            Notification.show("Error al cargar el proyecto: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate(""); // Redirigir si ocurre un error
        }
    }

    // Método para ajustar los campos según su editabilidad
    private void setFieldsEditable(boolean editable) {
        titulo.setReadOnly(!editable);
        nombrecorto.setReadOnly(!editable);
        memoria.setVisible(editable);
        select.setReadOnly(!editable);
        importancia.setReadOnly(!editable);
        interesados.setReadOnly(!editable);
        financiacion.setReadOnly(!editable);
        checkboxGroup.setEnabled(editable);
        alcance.setReadOnly(!editable);
        fechaObjetivo.setReadOnly(!editable);
        normativa.setReadOnly(!editable);
        especificaciones.setVisible(editable);
        presupuesto.setVisible(editable);
        saveButton.setVisible(editable);

    }



    private void saveProject(Long id, String titulo, String nombrecorto, String select, String interesados, Double financiacion, String alcance, LocalDate fechaObjetivo, String normativa, Object checkboxGroup) {

        List<String> checkboxGroupList = new ArrayList<>();
        checkboxGroupList.addAll(new ArrayList<>( (Set<String>) checkboxGroup ));


        try {
            proyectoService.updateProject(id, titulo, nombrecorto, memoriaData, select, interesados, financiacion, alcance, fechaObjetivo, normativa, checkboxGroupList, especificacionesData, presupuestoData);
            Notification.show("Proyecto actualizado con éxito.", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("");


        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            System.err.println("Detalles del error: " + e.getCause());
            e.printStackTrace();
            Notification.show("Ocurrió un error al actualizar el proyecto." + e.getMessage(), 7000, Notification.Position.MIDDLE);
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
                Notification.show("Error al cargar ", 3000, Notification.Position.MIDDLE);
            }
        });

        upload.addFileRemovedListener(event -> {
            dataSetter.accept(null); // Limpia los datos almacenados
            Notification.show("Eliminado.", 3000, Notification.Position.MIDDLE);
        });

        return upload;
    }
}
