package sga;

import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudes {

    // Smell: Acoplamiento directo a la estructura de persistencia en memoria (Viola SRP y DIP)
    public static List<Solicitud> baseDeDatosSolicitudes = new ArrayList<>();
    public static List<Responsable> baseDeDatosResponsables = new ArrayList<>();
    public static List<String> logsAuditoria = new ArrayList<>();
    public static int ultimoId = 1000;

    // =========================================================================
    // OPERACIÓN 1: REGISTRAR SOLICITUD
    // Smells: Long Parameter List (9 parámetros), Long Method, Duplicated Code,
    // Switch/If-Else anidados, Feature Envy, Magic Numbers/Strings.
    // =========================================================================
    public String registrarSolicitud(String legajoEst, String nombreEst, String apellidoEst,
                                     String emailEst, String dniEst, int materiasEst,
                                     boolean cuotaAlDiaEst, String tipoSolicitud, String descripcion) {

        // Validación de estudiante (Código duplicado más abajo)
        if (legajoEst == null || legajoEst.trim().isEmpty()) {
            return "ERROR: Legajo vacío";
        }
        if (nombreEst == null || nombreEst.trim().isEmpty() || apellidoEst == null || apellidoEst.trim().isEmpty()) {
            return "ERROR: Nombre o apellido inválido";
        }
        if (emailEst == null || !emailEst.contains("@") || !emailEst.endsWith(".edu.ar")) {
            return "ERROR: El email debe ser institucional (@...edu.ar)";
        }
        if (dniEst == null || dniEst.length() < 7 || dniEst.length() > 8) {
            return "ERROR: DNI inválido";
        }
        if (descripcion == null || descripcion.trim().length() < 10) {
            return "ERROR: La descripción debe tener al menos 10 caracteres";
        }

        Estudiante est = new Estudiante(legajoEst, nombreEst, apellidoEst, emailEst, dniEst, materiasEst, cuotaAlDiaEst);

        // Smell: Feature Envy y Switch/If-Else gigante para calcular prioridad y días según tipo
        // Viola OCP (Open/Closed Principle): agregar un nuevo tipo obliga a modificar este metodo
        int prioridadCalculada = 3;
        int diasEstimados = 15;

        if (tipoSolicitud.equals("CERTIFICADO")) {
            if (!est.cuotaAlDia) {
                return "ERROR: No puede pedir certificado con cuota vencida";
            }
            diasEstimados = 3;
            if (est.materiasAprobadas > 20) {
                prioridadCalculada = 1; // Magic Number
            } else {
                prioridadCalculada = 2;
            }
        } else if (tipoSolicitud.equals("EQUIVALENCIA")) {
            diasEstimados = 20;
            if (est.materiasAprobadas >= 10 && est.cuotaAlDia) {
                prioridadCalculada = 2;
            } else {
                prioridadCalculada = 3;
            }
        } else if (tipoSolicitud.equals("EXCEPCION")) {
            diasEstimados = 5;
            if (est.materiasAprobadas > 15 && est.cuotaAlDia) {
                prioridadCalculada = 1;
            } else {
                prioridadCalculada = 2;
            }
        } else if (tipoSolicitud.equals("BAJA")) {
            diasEstimados = 2;
            prioridadCalculada = 1;
        } else {
            return "ERROR: Tipo de solicitud desconocido";
        }

        ultimoId++;
        String fechaHoy = "2026-10-25"; // Hardcoded / Magic String
        Solicitud nueva = new Solicitud(ultimoId, est, tipoSolicitud, prioridadCalculada, "PENDIENTE", descripcion, fechaHoy, diasEstimados);

        // Persistencia directa acoplada
        baseDeDatosSolicitudes.add(nueva);
        logsAuditoria.add("[LOG] Solicitud creada ID: " + ultimoId + " por legajo: " + legajoEst + " Prioridad: " + prioridadCalculada);

        return "OK: Solicitud registrada con ID " + ultimoId + " (Prioridad " + prioridadCalculada + ", Estimado: " + diasEstimados + " días)";
    }

    // =========================================================================
    // OPERACIÓN 2: ASIGNAR RESPONSABLE
    // Smells: Duplicated Code, Feature Envy (calcula capacidad fuera de Responsable),
    // condicionales complejos de áreas.
    // =========================================================================
    public String asignarResponsable(int idSolicitud, String idResponsable) {
        Solicitud sol = null;
        for (int i = 0; i < baseDeDatosSolicitudes.size(); i++) {
            if (baseDeDatosSolicitudes.get(i).id == idSolicitud) {
                sol = baseDeDatosSolicitudes.get(i);
                break;
            }
        }
        if (sol == null) {
            return "ERROR: Solicitud no encontrada";
        }

        // Smell: Duplicated Code (repite las mismas validaciones del estudiante por seguridad mal entendida)
        if (sol.estudiante.legajo == null || sol.estudiante.legajo.trim().isEmpty()) {
            return "ERROR: Legajo vacío";
        }
        if (sol.estudiante.email == null || !sol.estudiante.email.contains("@") || !sol.estudiante.email.endsWith(".edu.ar")) {
            return "ERROR: El email debe ser institucional (@...edu.ar)";
        }

        Responsable resp = null;
        for (int i = 0; i < baseDeDatosResponsables.size(); i++) {
            if (baseDeDatosResponsables.get(i).id.equals(idResponsable)) {
                resp = baseDeDatosResponsables.get(i);
                break;
            }
        }
        if (resp == null) {
            return "ERROR: Responsable no encontrado";
        }

        // Smell: Feature Envy (El gestor pregunta los datos internos a Responsable en lugar de que Responsable valide su disponibilidad)
        if (resp.solicitudesActivas >= resp.maximoPermitido) {
            return "ERROR: El responsable " + resp.nombreCompleto + " superó su límite de carga (" + resp.maximoPermitido + ")";
        }

        // Regla de correspondencia Tipo -> Área con cadenas mágicas
        if (sol.tipo.equals("CERTIFICADO") && !resp.area.equals("ALUMNOS")) {
            return "ERROR: Los certificados solo los gestiona el área ALUMNOS";
        }
        if (sol.tipo.equals("EQUIVALENCIA") && !resp.area.equals("DIRECCION")) {
            return "ERROR: Las equivalencias solo las gestiona DIRECCION";
        }
        if (sol.tipo.equals("EXCEPCION") && !resp.area.equals("EXAMENES")) {
            return "ERROR: Las excepciones solo las gestiona EXAMENES";
        }

        sol.responsableAsignado = resp;
        sol.estado = "EN_REVISION";
        resp.solicitudesActivas = resp.solicitudesActivas + 1;

        logsAuditoria.add("[LOG] Solicitud " + idSolicitud + " asignada a " + resp.nombreCompleto);
        return "OK: Solicitud " + idSolicitud + " asignada a " + resp.nombreCompleto + " y pasada a EN_REVISION";
    }

    // =========================================================================
    // OPERACIÓN 3: CAMBIAR ESTADO DE SOLICITUD
    // Smells: Lógica de transición de estados fuera de la entidad Solicitud (Move Method candidato),
    // Magic Strings, Duplicated lookup loop.
    // =========================================================================
    public String cambiarEstadoSolicitud(int idSolicitud, String nuevoEstado, String observacion) {
        // Búsqueda repetida (Duplicated Code con respecto a asignarResponsable)
        Solicitud sol = null;
        for (int i = 0; i < baseDeDatosSolicitudes.size(); i++) {
            if (baseDeDatosSolicitudes.get(i).id == idSolicitud) {
                sol = baseDeDatosSolicitudes.get(i);
                break;
            }
        }
        if (sol == null) {
            return "ERROR: Solicitud no encontrada";
        }

        if (!nuevoEstado.equals("PENDIENTE") && !nuevoEstado.equals("EN_REVISION") &&
                !nuevoEstado.equals("APROBADA") && !nuevoEstado.equals("RECHAZADA")) {
            return "ERROR: Estado inválido";
        }

        if (sol.estado.equals("APROBADA") || sol.estado.equals("RECHAZADA")) {
            return "ERROR: No se puede modificar una solicitud que ya está cerrada (" + sol.estado + ")";
        }

        if ((nuevoEstado.equals("APROBADA") || nuevoEstado.equals("RECHAZADA")) && sol.responsableAsignado == null) {
            return "ERROR: No se puede aprobar o rechazar sin tener un responsable asignado";
        }

        if (nuevoEstado.equals("RECHAZADA") && (observacion == null || observacion.trim().length() < 5)) {
            return "ERROR: Para rechazar debe indicar un motivo válido";
        }

        sol.estado = nuevoEstado;
        sol.observacionResolucion = observacion;

        // Si se cierra, liberar cupo del responsable
        if ((nuevoEstado.equals("APROBADA") || nuevoEstado.equals("RECHAZADA")) && sol.responsableAsignado != null) {
            sol.responsableAsignado.solicitudesActivas = sol.responsableAsignado.solicitudesActivas - 1;
        }

        logsAuditoria.add("[LOG] Solicitud " + idSolicitud + " cambió a estado " + nuevoEstado);
        return "OK: Estado actualizado a " + nuevoEstado;
    }

    // =========================================================================
    // OPERACIÓN 4: BUSCAR Y FILTRAR SOLICITUDES
    // =========================================================================
    public List<Solicitud> buscarSolicitudes(String filtroEstado, String filtroTipo, int filtroPrioridad) {
        List<Solicitud> resultado = new ArrayList<>();
        for (int i = 0; i < baseDeDatosSolicitudes.size(); i++) {
            Solicitud s = baseDeDatosSolicitudes.get(i);
            boolean coincide = true;

            if (filtroEstado != null && !filtroEstado.isEmpty() && !s.estado.equals(filtroEstado)) {
                coincide = false;
            }
            if (filtroTipo != null && !filtroTipo.isEmpty() && !s.tipo.equals(filtroTipo)) {
                coincide = false;
            }
            if (filtroPrioridad > 0 && s.prioridad != filtroPrioridad) {
                coincide = false;
            }

            if (coincide) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    // =========================================================================
    // OPERACIÓN 5: GENERAR REPORTE SIMPLE
    // Smells: Mezcla cálculo estadístico con formateo de presentación en texto plano (Viola SRP).
    // =========================================================================
    public String generarReporteGeneral() {
        int total = baseDeDatosSolicitudes.size();
        int pendientes = 0;
        int enRevision = 0;
        int aprobadas = 0;
        int rechazadas = 0;
        int prioridadAlta = 0;

        for (int i = 0; i < baseDeDatosSolicitudes.size(); i++) {
            Solicitud s = baseDeDatosSolicitudes.get(i);
            if (s.estado.equals("PENDIENTE")) pendientes++;
            if (s.estado.equals("EN_REVISION")) enRevision++;
            if (s.estado.equals("APROBADA")) aprobadas++;
            if (s.estado.equals("RECHAZADA")) rechazadas++;
            if (s.prioridad == 1) prioridadAlta++;
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