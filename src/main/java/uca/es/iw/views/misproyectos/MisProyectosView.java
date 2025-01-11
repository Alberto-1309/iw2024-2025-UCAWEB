package uca.es.iw.views.misproyectos;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import uca.es.iw.data.Proyecto;
import uca.es.iw.data.ProyectoRepository;
import uca.es.iw.data.UserRepository;

import java.util.List;

@Route(value = "mis-proyectos", layout = uca.es.iw.views.MainLayout.class)
@Menu(order = 4, icon = "line-awesome/svg/file.svg")
@RolesAllowed("USER") // Ajusta el rol según sea necesario
public class MisProyectosView extends VerticalLayout {

    private final ProyectoRepository proyectoRepository;
    private final UserRepository userRepository;
    private final I18NProvider i18nProvider;

    private final Grid<Proyecto> grid = new Grid<>(Proyecto.class);

    @Autowired
    public MisProyectosView(ProyectoRepository proyectoRepository, UserRepository userRepository, I18NProvider i18nProvider) {
        this.proyectoRepository = proyectoRepository;
        this.userRepository = userRepository;
        this.i18nProvider = i18nProvider;

        configureGrid();
        loadGridData();

        add(grid);
    }

    private void configureGrid() {
        // Limpia cualquier columna automática generada
        grid.setColumns();

        // Configura manualmente las columnas que quieres mostrar con traducciones
        grid.addColumn(new ComponentRenderer<>(proyecto -> {
            Anchor anchor = new Anchor(String.format("project-view/%s", proyecto.getId()), proyecto.getTitulo());
            anchor.getElement().setAttribute("theme", "tertiary");
            return anchor;
        })).setHeader(i18nProvider.getTranslation("mis_proyectos.titulo", getLocale())).setAutoWidth(true);

        grid.addColumn(Proyecto::getNombreSolicitante)
                .setHeader(i18nProvider.getTranslation("mis_proyectos.solicitante", getLocale()))
                .setAutoWidth(true);

        grid.addColumn(Proyecto::getEstado)
                .setHeader(i18nProvider.getTranslation("mis_proyectos.estado", getLocale()))
                .setAutoWidth(true);

        grid.addColumn(Proyecto::getFechaCreado)
                .setHeader(i18nProvider.getTranslation("mis_proyectos.fecha_creacion", getLocale()))
                .setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void loadGridData() {
        Long userId = getAuthenticatedUserId();
        if (userId != null) {
            List<Proyecto> proyectos = proyectoRepository.findByCreadoId(userId);
            if (proyectos.isEmpty()) {
                Notification.show(i18nProvider.getTranslation("mis_proyectos.notificacion.sin_proyectos", getLocale()));
            } else {
                grid.setItems(proyectos);
            }
        } else {
            Notification.show(i18nProvider.getTranslation("mis_proyectos.notificacion.usuario_no_autenticado", getLocale()));
        }
    }

    private Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .map(user -> user.getId()) // Ajusta según cómo está definido el ID en tu entidad User
                    .orElse(null);
        }
        return null;
    }
}