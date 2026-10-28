package sga;

public class Responsable {
    private String id;
    private String nombreCompleto;
    private String area;
    private int solicitudesActivas;
    private int maximoPermitido;

    public Responsable(String id, String nombreCompleto, String area, int solicitudesActivas, int maximoPermitido) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.area = area;
        this.solicitudesActivas = solicitudesActivas;
        this.maximoPermitido = maximoPermitido;
    }

    public String getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getArea() { return area; }
    public int getSolicitudesActivas() { return solicitudesActivas; }
    public int getMaximoPermitido() { return maximoPermitido; }
    
    public void setSolicitudesActivas(int solicitudesActivas) { this.solicitudesActivas = solicitudesActivas; }
}