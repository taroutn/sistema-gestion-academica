package sga;

import java.util.ArrayList;
import java.util.List;

public class InMemorySolicitudRepository implements SolicitudRepository {
    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final List<Responsable> responsables = new ArrayList<>();
    private final List<String> logs = new ArrayList<>();

    @Override
    public void guardar(Solicitud solicitud) { solicitudes.add(solicitud); }

    @Override
    public List<Solicitud> obtenerTodas() { return solicitudes; }

    @Override
    public Solicitud buscarPorId(int id) {
        for (Solicitud s : solicitudes) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    @Override
    public void guardarResponsable(Responsable responsable) { responsables.add(responsable); }

    @Override
    public List<Responsable> obtenerTodosLosResponsables() { return responsables; }

    @Override
    public Responsable buscarResponsablePorId(String id) {
        for (Responsable r : responsables) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    @Override
    public void registrarLog(String log) { logs.add(log); }

    @Override
    public int cantidadLogs() { return logs.size(); }
}