package uca.es.iw.data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id_proyecto;

    @Column(name = "titulo", nullable = false, length = 60)
    private String titulo;

    @Column(name = "nombre_corto", nullable = false, length = 15)
    private String nombreCorto;

    @Lob
    @Column(name = "memoria", nullable = false, columnDefinition="MEDIUMBLOB")
    private byte[] memoria;

    @Column(name = "nombre_solicitante", nullable = false, length = 60)
    private String nombreSolicitante;

    @Column(name = "correo_solicitante", nullable = false, length = 100)
    private String correoSolicitante;

    @Column(name = "unidad_solicitante", nullable = false, length = 100)
    private String unidadSolicitante;

    @Column(name = "promotor", nullable = false, length = 60)
    private String promotor;

    @Column(name = "importancia", nullable = false)
    private Integer importancia;

    @Column(name = "interesados", nullable = false, columnDefinition = "TEXT")
    private String interesados;

    @Column(name = "financiacion", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double financiacion;

    @Column(name = "aoe1", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe1;

    @Column(name = "aoe2", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe2;

    @Column(name = "aoe3", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe3;

    @Column(name = "aoe4", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe4;

    @Column(name = "aoe5", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe5;

    @Column(name = "aoe6", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe6;

    @Column(name = "aoe7", nullable = true, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean aoe7;

    @Column(name = "alcance", nullable = false, length = 100)
    private String alcance;

    @Column(name = "fecha_objetivo", nullable = false)
    private LocalDate fechaObjetivo;

    @Column(name = "normativa", nullable = true, length = 100)
    private String normativa;

    @Lob
    @Column(name = "especificaciones", nullable = true, columnDefinition="MEDIUMBLOB")
    private byte[] especificaciones;

    @Lob
    @Column(name = "presupuestos", nullable = true, columnDefinition="MEDIUMBLOB")
    private byte[] presupuestos;

    @Column(name = "fecha_creado", nullable = false)
    private LocalDate fechaCreado;

    @Column(name = "cal_oportunidad", nullable = true, columnDefinition = "DECIMAL(10,2)")
    private Double calOportunidad;

    @Column(name = "cal_tecnica", nullable = true, columnDefinition = "DECIMAL(10,2)")
    private Double calTecnica;

    @Column(name = "cal_disponibilidad", nullable = true, columnDefinition = "DECIMAL(10,2)")
    private Double calDisponibilidad;

    @Column(name = "cal_final", nullable = true, columnDefinition = "DECIMAL(10,2)")
    private Double calFinal;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "financiacion_necesaria", nullable = true, columnDefinition = "DECIMAL(10,2)")
    private Double financiacionNecesaria;

    @Column(name = "recursos_humanos_necesarios", nullable = true)
    private Integer recursosHumanosNecesarios;

    @Column(name = "calificado", nullable = false, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean calificado;

    @Column(name = "creado_id", nullable = false, columnDefinition="BIGINT")
    private Long creadoId;

    @Column(name = "archivado", nullable = false, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean archivado;

    // Relación con Convocatoria
    @ManyToOne
    @JoinColumn(name = "convocatoria_id", nullable = false)
    private Convocatoria convocatoria;

    // Getters y setters para Convocatoria
    public Convocatoria getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(Convocatoria convocatoria) {
        this.convocatoria = convocatoria;
    }

    // Getters y setters restantes
    public Long getId() {
        return Id_proyecto;
    }

    public void setId(Long proyectoId) {
        Id_proyecto = proyectoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorreoSolicitante() {
        return correoSolicitante;
    }

    public void setCorreoSolicitante(String correoSolicitante) {
        this.correoSolicitante = correoSolicitante;
    }

    public String getUnidadSolicitante() {
        return unidadSolicitante;
    }

    public void setUnidadSolicitante(String unidadSolicitante) {
        this.unidadSolicitante = unidadSolicitante;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public byte[] getMemoria() {
        return memoria;
    }

    public void setMemoria(byte[] memoria) {
        this.memoria = memoria;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getPromotor() {
        return promotor;
    }

    public void setPromotor(String promotor) {
        this.promotor = promotor;
    }

    public Integer getImportancia() {
        return importancia;
    }

    public void setImportancia(Integer importancia) {
        this.importancia = importancia;
    }

    public String getInteresados() {
        return interesados;
    }

    public void setInteresados(String interesados) {
        this.interesados = interesados;
    }

    public Double getFinanciacion() {
        return financiacion;
    }

    public void setFinanciacion(Double financiacion) {
        this.financiacion = financiacion;
    }

    public Boolean getAoe1() {
        return aoe1;
    }

    public void setAoe1(Boolean aoe1) {
        this.aoe1 = aoe1;
    }

    public Boolean getAoe2() {
        return aoe2;
    }

    public void setAoe2(Boolean aoe2) {
        this.aoe2 = aoe2;
    }

    public Boolean getAoe3() {
        return aoe3;
    }

    public void setAoe3(Boolean aoe3) {
        this.aoe3 = aoe3;
    }

    public Boolean getAoe4() {
        return aoe4;
    }

    public void setAoe4(Boolean aoe4) {
        this.aoe4 = aoe4;
    }

    public Boolean getAoe5() {
        return aoe5;
    }

    public void setAoe5(Boolean aoe5) {
        this.aoe5 = aoe5;
    }

    public Boolean getAoe6() {
        return aoe6;
    }

    public void setAoe6(Boolean aoe6) {
        this.aoe6 = aoe6;
    }

    public Boolean getAoe7() {
        return aoe7;
    }

    public void setAoe7(Boolean aoe7) {
        this.aoe7 = aoe7;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public LocalDate getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(LocalDate fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public String getNormativa() {
        return normativa;
    }

    public void setNormativa(String normativa) {
        this.normativa = normativa;
    }

    public byte[] getEspecificaciones() {
        return especificaciones;
    }

    public void setEspecificaciones(byte[] especificaciones) {
        this.especificaciones = especificaciones;
    }

    public byte[] getPresupuestos() {
        return presupuestos;
    }

    public void setPresupuestos(byte[] presupuestos) {
        this.presupuestos = presupuestos;
    }

    public LocalDate getFechaCreado() {
        return fechaCreado;
    }

    public void setFechaCreado(LocalDate fechaCreado) {
        this.fechaCreado = fechaCreado;
    }

    public Double getCalOportunidad() {
        return calOportunidad;
    }

    public void setCalOportunidad(Double calOportunidad) {
        this.calOportunidad = calOportunidad;
    }

    public Double getCalDisponibilidad() {
        return calDisponibilidad;
    }

    public void setCalDisponibilidad(Double calDisponibilidad) {
        this.calDisponibilidad = calDisponibilidad;
    }

    public Double getCalFinal() {
        return calFinal;
    }

    public void setCalFinal(Double calFinal) {
        this.calFinal = calFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getCalTecnica() {
        return calTecnica;
    }

    public void setCalTecnica(Double calTecnica) {
        this.calTecnica = calTecnica;
    }

    public Double getFinanciacionNecesaria() {
        return financiacionNecesaria;
    }

    public void setFinanciacionNecesaria(Double financiacionNecesaria) {
        this.financiacionNecesaria = financiacionNecesaria;
    }

    public Integer getRecursosHumanosNecesarios() {
        return recursosHumanosNecesarios;
    }

    public void setRecursosHumanosNecesarios(Integer recursosHumanosNecesarios) {
        this.recursosHumanosNecesarios = recursosHumanosNecesarios;
    }

    public Boolean getCalificado() {
        return calificado;
    }

    public void setCalificado(Boolean calificado) {
        this.calificado = calificado;
    }

    public Long getCreadoId() {
        return creadoId;
    }

    public void setCreadoId(Long creadoId) {
        this.creadoId = creadoId;
    }

    public Boolean getArchivado() {
        return archivado;
    }

    public void setArchivado(Boolean archivado) {
        this.archivado = archivado;
    }
}
