package sga;

import java.util.List;

public interface SolicitudRepository {
    void guardar(Solicitud solicitud);
    List<Solicitud> obtenerTodas();
    Solicitud buscarPorId(int id);
    
    void guardarResponsable(Responsable responsable);
    List<Responsable> obtenerTodosLosResponsables();
    Responsable buscarResponsablePorId(String id);
    
    void registrarLog(String log);
    int cantidadLogs();
}