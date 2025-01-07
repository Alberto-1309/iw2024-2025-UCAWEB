package uca.es.iw.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
    Optional<Convocatoria> findByFechaAperturaBeforeAndFechaCierreAfter(LocalDate apertura, LocalDate cierre);
}