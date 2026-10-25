package sga;

public class Solicitud {
    public int id;
    public Estudiante estudiante;
    // Smell: Primitive Obsession (tipo, estado y prioridad como String/int sueltos en vez de Enums o clases)
    public String tipo;        // "EQUIVALENCIA", "CERTIFICADO", "EXCEPCION", "BAJA"
    public int prioridad;      // 1 = Alta, 2 = Media, 3 = Baja
    public String estado;      // "PENDIENTE", "EN_REVISION", "APROBADA", "RECHAZADA"
    public String descripcion;
    public String fechaCreacion;
    public int diasEstimadosResolucion;
    public Responsable responsableAsignado;
    public String observacionResolucion;

    public Solicitud(int id, Estudiante estudiante, String tipo, int prioridad, String estado,
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
}