package uca.es.iw.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import uca.es.iw.data.Proyecto;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long>, JpaSpecificationExecutor<Proyecto> {
    @Query(value = """

            SELECT nombre_corto
    FROM Proyecto
    WHERE estado = 'Pendiente'
    ORDER BY nombre_corto ASC;
    """, nativeQuery = true)
    List<String> findPendingProjects();

    Optional<Proyecto> findByNombreCorto(String nombreCorto);
}
