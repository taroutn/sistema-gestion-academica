package sga;

public class Estudiante {
    private String legajo;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private int materiasAprobadas;
    private boolean cuotaAlDia;

    public Estudiante(String legajo, String nombre, String apellido, String email, String dni, int materiasAprobadas, boolean cuotaAlDia) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.dni = dni;
        this.materiasAprobadas = materiasAprobadas;
        this.cuotaAlDia = cuotaAlDia;
    }

    public String getLegajo() { return legajo; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getDni() { return dni; }
    public int getMateriasAprobadas() { return materiasAprobadas; }
    public boolean isCuotaAlDia() { return cuotaAlDia; }
}