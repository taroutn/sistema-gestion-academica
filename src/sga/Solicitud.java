package sga;

public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private TipoSolicitud tipo;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private String descripcion;
    private String fechaCreacion;
    private int diasEstimadosResolucion;
    private Responsable responsableAsignado;
    private String observacionResolucion;

    public Solicitud(int id, Estudiante estudiante, TipoSolicitud tipo, Prioridad prioridad, EstadoSolicitud estado,
                     String descripcion, String fechaCreacion, int diasEstimadosResolucion) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.diasEstimadosResolucion = diasEstimadosResolucion;
        this.responsableAsignado = null;
        this.observacionResolucion = "";
    }

    public int getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public TipoSolicitud getTipo() { return tipo; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoSolicitud getEstado() { return estado; }
    public String getDescripcion() { return descripcion; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getDiasEstimadosResolucion() { return diasEstimadosResolucion; }
    public Responsable getResponsableAsignado() { return responsableAsignado; }
    public String getObservacionResolucion() { return observacionResolucion; }

    public void setResponsableAsignado(Responsable responsableAsignado) { this.responsableAsignado = responsableAsignado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public void setObservacionResolucion(String observacionResolucion) { this.observacionResolucion = observacionResolucion; }
}