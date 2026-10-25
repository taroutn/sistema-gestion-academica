package sga;

public class Responsable {
    public String id;
    public String nombreCompleto;
    public String area; // "ALUMNOS", "EXAMENES", "TESORERIA", "DIRECCION"
    public int solicitudesActivas;
    public int maximoPermitido;

    public Responsable(String id, String nombreCompleto, String area, int solicitudesActivas, int maximoPermitido) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.area = area;
        this.solicitudesActivas = solicitudesActivas;
        this.maximoPermitido = maximoPermitido;
    }
}