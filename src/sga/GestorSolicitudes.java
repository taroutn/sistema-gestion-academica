package sga;

import java.util.List;

public class GestorSolicitudes {

    // Ahora dependemos de la interfaz (DIP) y no de la implementación
    private final SolicitudRepository repository;
    private int ultimoId = 1000;

    // Inyección de dependencias por constructor
    public GestorSolicitudes(SolicitudRepository repository) {
        this.repository = repository;
    }

    public String registrarSolicitud(RegistroSolicitudRequest req) {
        String errorValidacion = validarDatosEstudiante(req);
        if (errorValidacion != null) return errorValidacion;

        Estudiante est = new Estudiante(req.legajoEst, req.nombreEst, req.apellidoEst, req.emailEst, req.dniEst, req.materiasEst, req.cuotaAlDiaEst);
        int[] calculo = calcularPrioridadYDias(est, req.tipoSolicitud);
        if (calculo == null) return "ERROR: No puede pedir certificado con cuota vencida o tipo desconocido";
        
        int prioridadCalculada = calculo[0];
        int diasEstimados = calculo[1];
        ultimoId++;
        
        TipoSolicitud tipoEnum = TipoSolicitud.valueOf(req.tipoSolicitud);
        Prioridad prioridadEnum = Prioridad.values()[prioridadCalculada - 1];
        
        Solicitud nueva = new Solicitud(ultimoId, est, tipoEnum, prioridadEnum, EstadoSolicitud.PENDIENTE, req.descripcion, "2026-10-25", diasEstimados);
        repository.guardar(nueva);
        
        repository.registrarLog("[LOG] Solicitud creada ID: " + ultimoId + " por legajo: " + req.legajoEst + " Prioridad: " + prioridadCalculada);
        return "OK: Solicitud registrada con ID " + ultimoId + " (Prioridad " + prioridadCalculada + ", Estimado: " + diasEstimados + " días)";
    }

    private String validarDatosEstudiante(RegistroSolicitudRequest req) {
        if (req.legajoEst == null || req.legajoEst.trim().isEmpty()) return "ERROR: Legajo vacío";
        if (req.nombreEst == null || req.nombreEst.trim().isEmpty() || req.apellidoEst == null || req.apellidoEst.trim().isEmpty()) return "ERROR: Nombre o apellido inválido";
        if (req.emailEst == null || !req.emailEst.contains("@") || !req.emailEst.endsWith(".edu.ar")) return "ERROR: El email debe ser institucional (@...edu.ar)";
        if (req.dniEst == null || req.dniEst.length() < 7 || req.dniEst.length() > 8) return "ERROR: DNI inválido";
        if (req.descripcion == null || req.descripcion.trim().length() < 10) return "ERROR: La descripción debe tener al menos 10 caracteres";
        return null; 
    }

    private int[] calcularPrioridadYDias(Estudiante est, String tipoSolicitud) {
        int prioridadCalculada = 3;
        int diasEstimados = 15;
        if (tipoSolicitud.equals("CERTIFICADO")) {
            if (!est.isCuotaAlDia()) return null;
            diasEstimados = 3;
            prioridadCalculada = (est.getMateriasAprobadas() > 20) ? 1 : 2;
        } else if (tipoSolicitud.equals("EQUIVALENCIA")) {
            diasEstimados = 20;
            prioridadCalculada = (est.getMateriasAprobadas() >= 10 && est.isCuotaAlDia()) ? 2 : 3;
        } else if (tipoSolicitud.equals("EXCEPCION")) {
            diasEstimados = 5;
            prioridadCalculada = (est.getMateriasAprobadas() > 15 && est.isCuotaAlDia()) ? 1 : 2;
        } else if (tipoSolicitud.equals("BAJA")) {
            diasEstimados = 2;
            prioridadCalculada = 1;
        } else {
            return null; 
        }
        return new int[]{prioridadCalculada, diasEstimados};
    }

    public String asignarResponsable(int idSolicitud, String idResponsable) {
        Solicitud sol = repository.buscarPorId(idSolicitud);
        if (sol == null) return "ERROR: Solicitud no encontrada";

        Responsable resp = repository.buscarResponsablePorId(idResponsable);
        if (resp == null) return "ERROR: Responsable no encontrado";

        if (!resp.tieneCupoDisponible()) {
            return "ERROR: El responsable " + resp.getNombreCompleto() + " superó su límite de carga (" + resp.getMaximoPermitido() + ")";
        }

        String errorArea = verificarCorrespondenciaArea(sol.getTipo(), resp.getArea());
        if (errorArea != null) return errorArea;

        sol.setResponsableAsignado(resp);
        sol.setEstado(EstadoSolicitud.EN_REVISION);
        resp.incrementarCarga();

        repository.registrarLog("[LOG] Solicitud " + idSolicitud + " asignada a " + resp.getNombreCompleto());
        return "OK: Solicitud " + idSolicitud + " asignada a " + resp.getNombreCompleto() + " y pasada a EN_REVISION";
    }

    private String verificarCorrespondenciaArea(TipoSolicitud tipo, String area) {
        if (tipo == TipoSolicitud.CERTIFICADO && !area.equals("ALUMNOS")) return "ERROR: Los certificados solo los gestiona el área ALUMNOS";
        if (tipo == TipoSolicitud.EQUIVALENCIA && !area.equals("DIRECCION")) return "ERROR: Las equivalencias solo las gestiona DIRECCION";
        if (tipo == TipoSolicitud.EXCEPCION && !area.equals("EXAMENES")) return "ERROR: Las excepciones solo las gestiona EXAMENES";
        return null;
    }

    public String cambiarEstadoSolicitud(int idSolicitud, String nuevoEstado, String observacion) {
        Solicitud sol = repository.buscarPorId(idSolicitud);
        if (sol == null) return "ERROR: Solicitud no encontrada";

        EstadoSolicitud estadoEnum;
        try {
            estadoEnum = EstadoSolicitud.valueOf(nuevoEstado);
        } catch (IllegalArgumentException e) {
            return "ERROR: Estado inválido";
        }

        if (sol.getEstado() == EstadoSolicitud.APROBADA || sol.getEstado() == EstadoSolicitud.RECHAZADA) {
            return "ERROR: No se puede modificar una solicitud que ya está cerrada (" + sol.getEstado() + ")";
        }

        if ((estadoEnum == EstadoSolicitud.APROBADA || estadoEnum == EstadoSolicitud.RECHAZADA) && sol.getResponsableAsignado() == null) {
            return "ERROR: No se puede aprobar o rechazar sin tener un responsable asignado";
        }

        if (estadoEnum == EstadoSolicitud.RECHAZADA && (observacion == null || observacion.trim().length() < 5)) {
            return "ERROR: Para rechazar debe indicar un motivo válido";
        }

        sol.setEstado(estadoEnum);
        sol.setObservacionResolucion(observacion);

        if ((estadoEnum == EstadoSolicitud.APROBADA || estadoEnum == EstadoSolicitud.RECHAZADA) && sol.getResponsableAsignado() != null) {
            sol.getResponsableAsignado().liberarCarga();
        }

        repository.registrarLog("[LOG] Solicitud " + idSolicitud + " cambió a estado " + nuevoEstado);
        return "OK: Estado actualizado a " + nuevoEstado;
    }

    public List<Solicitud> buscarSolicitudes(String filtroEstado, String filtroTipo, int filtroPrioridad) {
        List<Solicitud> todas = repository.obtenerTodas();
        todas.removeIf(s -> {
            boolean noCoincideEstado = filtroEstado != null && !filtroEstado.isEmpty() && !s.getEstado().name().equals(filtroEstado);
            boolean noCoincideTipo = filtroTipo != null && !filtroTipo.isEmpty() && !s.getTipo().name().equals(filtroTipo);
            boolean noCoincidePrioridad = filtroPrioridad > 0 && s.getPrioridad().getNivel() != filtroPrioridad;
            return noCoincideEstado || noCoincideTipo || noCoincidePrioridad;
        });
        return todas;
    }

    public String generarReporteGeneral() {
        List<Solicitud> todas = repository.obtenerTodas();
        int total = todas.size();
        int pendientes = 0, enRevision = 0, aprobadas = 0, rechazadas = 0, prioridadAlta = 0;

        for (Solicitud s : todas) {
            if (s.getEstado() == EstadoSolicitud.PENDIENTE) pendientes++;
            if (s.getEstado() == EstadoSolicitud.EN_REVISION) enRevision++;
            if (s.getEstado() == EstadoSolicitud.APROBADA) aprobadas++;
            if (s.getEstado() == EstadoSolicitud.RECHAZADA) rechazadas++;
            if (s.getPrioridad() == Prioridad.ALTA) prioridadAlta++;
        }

        String rep = "=== REPORTE DE SOLICITUDES ACADÉMICAS ===\n";
        rep += "Total registradas: " + total + "\n";
        rep += " - Pendientes: " + pendientes + "\n";
        rep += " - En Revisión: " + enRevision + "\n";
        rep += " - Aprobadas: " + aprobadas + "\n";
        rep += " - Rechazadas: " + rechazadas + "\n";
        rep += "Solicitudes de Prioridad Alta (1): " + prioridadAlta + "\n";
        rep += "Eventos de auditoría registrados: " + repository.cantidadLogs() + "\n";
        rep += "=========================================";
        return rep;
    }
}