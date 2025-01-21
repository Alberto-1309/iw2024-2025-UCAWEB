package uca.es.iw.views.avalar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import uca.es.iw.data.Proyecto;
import uca.es.iw.data.ProyectoRepository;
import uca.es.iw.data.User;
import uca.es.iw.data.UserRepository;
import uca.es.iw.services.ProyectoService;
import uca.es.iw.views.project.ProjectView;
import com.vaadin.flow.i18n.I18NProvider;

import java.util.List;

@Route(value = "avalar-proyectos/:proyectoID?/:action?(edit)", layout = uca.es.iw.views.MainLayout.class)
@Menu(order = 4, icon = "line-awesome/svg/file.svg")
@RolesAllowed("PROMOTOR")
public class PromotorView extends VerticalLayout {

    private final ProyectoRepository proyectoRepository;
    private final UserRepository userRepository;
    private final ProyectoService proyectoService;

    private final Grid<Proyecto> grid = new Grid<>();
    private final I18NProvider i18nProvider;

    @Autowired
    public PromotorView(ProyectoRepository proyectoRepository, UserRepository userRepository, ProyectoService proyectoService, I18NProvider i18nProvider) {
        this.proyectoRepository = proyectoRepository;
        this.userRepository = userRepository;
        this.proyectoService = proyectoService;
        this.i18nProvider = i18nProvider;

        String promotor = getAuthenticatedPromotorName();

        configureGrid();
        loadGridData(promotor);

        add(grid);
    }

    private void configureGrid() {
        // Columna con enlace al proyecto
        grid.addColumn(new ComponentRenderer<>(proyecto -> {
            if (proyecto instanceof Proyecto) {
                Anchor anchor = new Anchor(String.format("project-view/%s", proyecto.getId()), proyecto.getTitulo());
                anchor.getElement().setAttribute("theme", "tertiary");
                return anchor;
            }
            return null;
        })).setHeader(i18nProvider.getTranslation("promotor_view.title", getLocale())).setAutoWidth(true);

        // Otras columnas que quieres mostrar
        grid.addColumn(Proyecto::getNombreSolicitante).setHeader(i18nProvider.getTranslation("promotor_view.solicitante", getLocale())).setAutoWidth(true);
        grid.addColumn(Proyecto::getEstado).setHeader(i18nProvider.getTranslation("promotor_view.estado", getLocale())).setAutoWidth(true);
        grid.addColumn(Proyecto::getFechaCreado).setHeader(i18nProvider.getTranslation("promotor_view.fecha_solicitud", getLocale())).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(proyecto -> {
            ComboBox<Integer> comboBox = new ComboBox<>();
            comboBox.setItems(1, 2, 3, 4, 5); // Opciones del 1 al 5
            comboBox.setValue(proyecto.getImportancia()); // Establecer valor actual (asegúrate de que exista el campo 'importancia' en Proyecto)

            // Escuchar cambios en el valor seleccionado
            comboBox.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    proyecto.setImportancia(event.getValue()); // Actualizar la importancia del proyecto
                    proyectoRepository.save(proyecto); // Guardar el cambio en la base de datos
                }
            });
            return comboBox;
        })).setHeader(i18nProvider.getTranslation("promotor_view.importancia", getLocale())).setAutoWidth(true);

        // Columna de acciones (Avalar y No Avalar)
        grid.addComponentColumn(this::createActionButtons).setHeader(i18nProvider.getTranslation("promotor_view.acciones", getLocale())).setAutoWidth(true);

        // Opcional: puedes deshabilitar bordes
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void loadGridData(String promotor) {
        // Obtienes los proyectos pendientes para un promotor
        List<Proyecto> proyectosPendientes = proyectoRepository.findByPromotorAndEstado(promotor, "PENDIENTE");

        // Si no hay proyectos pendientes, muestra una notificación
        if (proyectosPendientes.isEmpty()) {
            Notification.show(i18nProvider.getTranslation("promotor_view.no_proyectos", getLocale()));
        } else {
            // Estableces los proyectos al grid
            grid.setItems(proyectosPendientes);
        }
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private String getAuthenticatedPromotorName() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .map(User::getName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private HorizontalLayout createActionButtons(Proyecto proyecto) {
        Button avalarButton = new Button(
                i18nProvider.getTranslation("promotor_view.avalar", getLocale()),
                event -> {
                    if (proyecto.getImportancia() == null) {
                        Notification.show(
                                i18nProvider.getTranslation("promotor_view.error.sin_importancia", getLocale()),
                                3000,
                                Notification.Position.TOP_CENTER
                        );
                    } else {
                        gestionarProyecto(proyecto, "En curso");
                    }
                }
        );
        Button rechazarButton = new Button(i18nProvider.getTranslation("promotor_view.no_avalar", getLocale()), event -> gestionarProyecto(proyecto, "Rechazado"));

        return new HorizontalLayout(avalarButton, rechazarButton);
    }


    private void gestionarProyecto(Proyecto proyecto, String nuevoEstado) {
        proyecto.setEstado(nuevoEstado);
        proyectoRepository.save(proyecto);

        // Actualiza el grid después de la acción
        grid.getListDataView().removeItem(proyecto);

        Notification.show(i18nProvider.getTranslation("promotor_view.proyecto_estado", getLocale()) + ": " + proyecto.getTitulo(), 3000, Notification.Position.TOP_CENTER);
    }
}