package uca.es.iw.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecursosRepository extends JpaRepository<Recursos, Long>, JpaSpecificationExecutor<Recursos> {

    @Query(value = """
            SELECT presupuesto_total
    FROM recursos
    WHERE id = 1;
    """, nativeQuery = true)
    Double findPresupuestoTotal();

    @Query(value = """
            SELECT recursos_humanos_total
    FROM recursos
    WHERE id = 1;
    """, nativeQuery = true)
    Integer findRecursosHumanosTotal();

    @Query(value = """
            SELECT presupuesto_restante
    FROM recursos
    WHERE id = 1;
    """, nativeQuery = true)
    Double findPresupuestoRestante();

    @Query(value = """
            SELECT recursos_humanos_restantes
    FROM recursos
    WHERE id = 1;
    """, nativeQuery = true)
    Integer findRecursosHumanosRestantes();

    Optional<Recursos> findByIdConvocatoria(long id);

    //List<Recursos> findByIdConvocatoria(long idConvocatoria);
}
