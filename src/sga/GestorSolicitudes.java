package sga;

import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {

    // Persistencia en memoria (por ahora la dejamos acá, luego la movemos a un Repository)
    public static List<Solicitud> baseDeDatosSolicitudes = new ArrayList<>();
    public static List<Responsable> baseDeDatosResponsables = new ArrayList<>();
    public static List<String> logsAuditoria = new ArrayList<>();
    public static int ultimoId = 1000;

    // =========================================================================
    // OPERACIÓN 1: REGISTRAR SOLICITUD (Refactorizado)
    // =========================================================================
    public String registrarSolicitud(RegistroSolicitudRequest req) {
        // 1. Extraemos la validación a un método aparte (Extract Method)
        String errorValidacion = validarDatosEstudiante(req);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        // 2. Creamos el estudiante
        Estudiante est = new Estudiante(req.legajoEst, req.nombreEst, req.apellidoEst, 
                                        req.emailEst, req.dniEst, req.materiasEst, req.cuotaAlDiaEst);

        // 3. Calculamos prioridad y días (Extract Method)
        int[] calculo = calcularPrioridadYDias(est, req.tipoSolicitud);
        if (calculo == null) {
            return "ERROR: No puede pedir certificado con cuota vencida o tipo desconocido";
        }
        
        int prioridadCalculada = calculo[0];
        int diasEstimados = calculo[1];

        ultimoId++;
        String fechaHoy = "2026-10-25";
        
        // Mapeamos el String del request al Enum correspondiente
        TipoSolicitud tipoEnum = TipoSolicitud.valueOf(req.tipoSolicitud);
        Prioridad prioridadEnum = Prioridad.values()[prioridadCalculada - 1]; // 1->ALTA, 2->MEDIA, 3->BAJA
        EstadoSolicitud estadoEnum = EstadoSolicitud.PENDIENTE;

        Solicitud nueva = new Solicitud(ultimoId, est, tipoEnum, prioridadEnum, estadoEnum, req.descripcion, fechaHoy, diasEstimados);

        baseDeDatosSolicitudes.add(nueva);
        logsAuditoria.add("[LOG] Solicitud creada ID: " + ultimoId + " por legajo: " + req.legajoEst + " Prioridad: " + prioridadCalculada);

        return "OK: Solicitud registrada con ID " + ultimoId + " (Prioridad " + prioridadCalculada + ", Estimado: " + diasEstimados + " días)";
    }

    // Métodos extraídos (Extract Method)
    private String validarDatosEstudiante(RegistroSolicitudRequest req) {
        if (req.legajoEst == null || req.legajoEst.trim().isEmpty()) return "ERROR: Legajo vacío";
        if (req.nombreEst == null || req.nombreEst.trim().isEmpty() || req.apellidoEst == null || req.apellidoEst.trim().isEmpty()) return "ERROR: Nombre o apellido inválido";
        if (req.emailEst == null || !req.emailEst.contains("@") || !req.emailEst.endsWith(".edu.ar")) return "ERROR: El email debe ser institucional (@...edu.ar)";
        if (req.dniEst == null || req.dniEst.length() < 7 || req.dniEst.length() > 8) return "ERROR: DNI inválido";
        if (req.descripcion == null || req.descripcion.trim().length() < 10) return "ERROR: La descripción debe tener al menos 10 caracteres";
        return null; // Todo OK
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
            return null; // Tipo desconocido
        }
        return new int[]{prioridadCalculada, diasEstimados};
    }

    // =========================================================================
    // OPERACIÓN 2: ASIGNAR RESPONSABLE (Refactorizado)
    // =========================================================================
    public String asignarResponsable(int idSolicitud, String idResponsable) {
        // 1. Buscamos la solicitud usando el método extraído (Mata el Duplicated Code)
        Solicitud sol = buscarSolicitudPorId(idSolicitud);
        if (sol == null) return "ERROR: Solicitud no encontrada";

        Responsable resp = buscarResponsablePorId(idResponsable);
        if (resp == null) return "ERROR: Responsable no encontrado";

        // 2. Validamos cupo (Feature Envy eliminado en el paso siguiente)
        if (resp.getSolicitudesActivas() >= resp.getMaximoPermitido()) {
            return "ERROR: El responsable " + resp.getNombreCompleto() + " superó su límite de carga (" + resp.getMaximoPermitido() + ")";
        }

        // 3. Validamos área
        String errorArea = verificarCorrespondenciaArea(sol.getTipo(), resp.getArea());
        if (errorArea != null) return errorArea;

        // 4. Asignamos
        sol.setResponsableAsignado(resp);
        sol.setEstado(EstadoSolicitud.EN_REVISION);
        resp.setSolicitudesActivas(resp.getSolicitudesActivas() + 1);

        logsAuditoria.add("[LOG] Solicitud " + idSolicitud + " asignada a " + resp.getNombreCompleto());
        return "OK: Solicitud " + idSolicitud + " asignada a " + resp.getNombreCompleto() + " y pasada a EN_REVISION";
    }

    private Solicitud buscarSolicitudPorId(int id) {
        for (Solicitud s : baseDeDatosSolicitudes) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    private Responsable buscarResponsablePorId(String id) {
        for (Responsable r : baseDeDatosResponsables) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    private String verificarCorrespondenciaArea(TipoSolicitud tipo, String area) {
        if (tipo == TipoSolicitud.CERTIFICADO && !area.equals("ALUMNOS")) return "ERROR: Los certificados solo los gestiona el área ALUMNOS";
        if (tipo == TipoSolicitud.EQUIVALENCIA && !area.equals("DIRECCION")) return "ERROR: Las equivalencias solo las gestiona DIRECCION";
        if (tipo == TipoSolicitud.EXCEPCION && !area.equals("EXAMENES")) return "ERROR: Las excepciones solo las gestiona EXAMENES";
        return null;
    }

    // =========================================================================
    // OPERACIÓN 3: CAMBIAR ESTADO (Refactorizado)
    // =========================================================================
    public String cambiarEstadoSolicitud(int idSolicitud, String nuevoEstado, String observacion) {
        Solicitud sol = buscarSolicitudPorId(idSolicitud);
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
            Responsable r = sol.getResponsableAsignado();
            r.setSolicitudesActivas(r.getSolicitudesActivas() - 1);
        }

        logsAuditoria.add("[LOG] Solicitud " + idSolicitud + " cambió a estado " + nuevoEstado);
        return "OK: Estado actualizado a " + nuevoEstado;
    }

    // =========================================================================
    // OPERACIÓN 4: BUSCAR Y FILTRAR (Refactorizado)
    // =========================================================================
    public List<Solicitud> buscarSolicitudes(String filtroEstado, String filtroTipo, int filtroPrioridad) {
        List<Solicitud> resultado = new ArrayList<>();
        for (Solicitud s : baseDeDatosSolicitudes) {
            boolean coincide = true;

            if (filtroEstado != null && !filtroEstado.isEmpty() && !s.getEstado().name().equals(filtroEstado)) coincide = false;
            if (filtroTipo != null && !filtroTipo.isEmpty() && !s.getTipo().name().equals(filtroTipo)) coincide = false;
            if (filtroPrioridad > 0 && s.getPrioridad().getNivel() != filtroPrioridad) coincide = false;

            if (coincide) resultado.add(s);
        }
        return resultado;
    }

    // =========================================================================
    // OPERACIÓN 5: GENERAR REPORTE (Refactorizado)
    // =========================================================================
    public String generarReporteGeneral() {
        int total = baseDeDatosSolicitudes.size();
        int pendientes = 0, enRevision = 0, aprobadas = 0, rechazadas = 0, prioridadAlta = 0;

        for (Solicitud s : baseDeDatosSolicitudes) {
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
        rep += "Eventos de auditoría registrados: " + logsAuditoria.size() + "\n";
        rep += "=========================================";
        return rep;
    }
}