package sga;

public class RegistroSolicitudRequest {
    public String legajoEst;
    public String nombreEst;
    public String apellidoEst;
    public String emailEst;
    public String dniEst;
    public int materiasEst;
    public boolean cuotaAlDiaEst;
    public String tipoSolicitud;
    public String descripcion;

    public RegistroSolicitudRequest(String legajoEst, String nombreEst, String apellidoEst, 
                                    String emailEst, String dniEst, int materiasEst, 
                                    boolean cuotaAlDiaEst, String tipoSolicitud, String descripcion) {
        this.legajoEst = legajoEst;
        this.nombreEst = nombreEst;
        this.apellidoEst = apellidoEst;
        this.emailEst = emailEst;
        this.dniEst = dniEst;
        this.materiasEst = materiasEst;
        this.cuotaAlDiaEst = cuotaAlDiaEst;
        this.tipoSolicitud = tipoSolicitud;
        this.descripcion = descripcion;
    }
}