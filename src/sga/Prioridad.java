package sga;

public enum Prioridad {
    ALTA(1),
    MEDIA(2),
    BAJA(3);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
}