package sga;

public class Estudiante {
    // Smell: Campos públicos sin encapsulamiento (violación de ocultamiento de información)
    public String legajo;
    public String nombre;
    public String apellido;
    public String email;
    public String dni;
    public int materiasAprobadas;
    public boolean cuotaAlDia;

    public Estudiante(String legajo, String nombre, String apellido, String email, String dni, int materiasAprobadas, boolean cuotaAlDia) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;
        this.materiasAprobadas = materiasAprobadas;
        this.cuotaAlDia = cuotaAlDia;
    }
}