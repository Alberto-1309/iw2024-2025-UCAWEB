package uca.es.iw.data;

import jakarta.persistence.*;

@Entity
public class Recursos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Column(name = "presupuestoTotal", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double presupuestoTotal;

    @Column(name = "recursosHumanosTotal", nullable = false)
    private Integer recursosHumanos;

    @Column(name = "presupuestoRestante", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double presupuestoRestante;

    @Column(name = "recursosHumanosRestantes", nullable = false)
    private Integer recursosHumanosRestantes;

    @Column (name = "idConvocatoria", nullable = false)
    private Long idConvocatoria;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPresupuestoTotal() {
        return presupuestoTotal;
    }

    public void setPresupuestoTotal(Double presupuestoTotal) {
        this.presupuestoTotal = presupuestoTotal;
    }

    public Integer getRecursosHumanos() {
        return recursosHumanos;
    }

    public void setRecursosHumanos(Integer recursosHumanos) {
        this.recursosHumanos = recursosHumanos;
    }

    public Double getPresupuestoRestante() {
        return presupuestoRestante;
    }

    public void setPresupuestoRestante(Double presupuestoRestante) {
        this.presupuestoRestante = presupuestoRestante;
    }

    public Integer getRecursosHumanosRestantes() {
        return recursosHumanosRestantes;
    }

    public void setRecursosHumanosRestantes(Integer recursosHumanosRestantes) {
        this.recursosHumanosRestantes = recursosHumanosRestantes;
    }

    public Long getIdConvocatoria() {
        return idConvocatoria;
    }

    public void setIdConvocatoria(Long idConvocatoria) {
        this.idConvocatoria = idConvocatoria;
    }
}
