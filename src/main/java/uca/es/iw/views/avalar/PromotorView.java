package uca.es.iw.views.avalar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import uca.es.iw.data.Proyecto;
import uca.es.iw.data.ProyectoRepository;
import uca.es.iw.data.User;
import uca.es.iw.data.UserRepository;
import uca.es.iw.views.project.ProjectView;

import java.util.List;

@Route("avalar-proyectos/:proyectoID?/:action?(edit)") // Ruta específica para los proyectos del promotor
@PageTitle("Proyectos Pendientess") // Título de la página
@Menu(order = 4, icon = "line-awesome/svg/file.svg") // Menú (debe estar configurado en un layout general)
@RolesAllowed("PROMOTOR")

public class PromotorView extends VerticalLayout {

    private final ProyectoRepository proyectoRepository;
    private final UserRepository userRepository;

    private Grid<Proyecto> grid = new Grid<>(Proyecto.class);

    @Autowired
    public PromotorView(ProyectoRepository proyectoRepository, UserRepository userRepository) {
        this.proyectoRepository = proyectoRepository;
        this.userRepository = userRepository;

        // Obtener el nombre del usuario autenticado
        String promotor = getAuthenticatedName();

        // Configurar Grid
        grid.addComponentColumn(this::createProjectLink).setHeader("Título");
        grid.addColumn(Proyecto::getNombreCorto).setHeader("Nombre Corto");
        grid.addColumn(Proyecto::getUnidadSolicitante).setHeader("Unidad Solicitante");
        grid.addComponentColumn(this::createActionButtons).setHeader("Acciones");

        // Cargar proyectos pendientes asignados al promotor
        List<Proyecto> proyectosPendientes = proyectoRepository.findByPromotorAndEstado(promotor, "PENDIENTE");
        grid.setItems(proyectosPendientes);

        // Añadir el grid al layout
        add(grid);
    }

    private RouterLink createProjectLink(Proyecto proyecto) {
    RouterLink link = new RouterLink(proyecto.getTitulo(), ProjectView.class);
    link.setQueryParameters(QueryParameters.simple(java.util.Map.of("proyectoID", String.valueOf(proyecto.getId()))));
    return link;
    }


    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private String getAuthenticatedName() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .map(User::getName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private HorizontalLayout createActionButtons(Proyecto proyecto) {
        Button avalarButton = new Button("Avalar", event -> gestionarProyecto(proyecto, true));
        Button rechazarButton = new Button("No Avalar", event -> gestionarProyecto(proyecto, false));

        return new HorizontalLayout(avalarButton, rechazarButton);
    }

    private void gestionarProyecto(Proyecto proyecto, boolean avalar) {
        // Actualizar el estado del proyecto
        if (avalar) {
            proyecto.setEstado("AVALADO");
        } else {
            proyecto.setEstado("NO_AVALADO");
        }
        proyectoRepository.save(proyecto);

        grid.getListDataView().removeItem(proyecto);

        // Mostrar notificación
        Notification.show("Proyecto actualizado: " + proyecto.getTitulo());
    }

}