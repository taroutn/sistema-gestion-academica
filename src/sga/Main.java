package sga;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Inyectamos la dependencia (DIP)
        SolicitudRepository repo = new InMemorySolicitudRepository();
        GestorSolicitudes gestor = new GestorSolicitudes(repo);

        // 2. Precarga de responsables usando el repositorio
        repo.guardarResponsable(new Responsable("RESP-01", "Laura Gómez", "ALUMNOS", 0, 2));
        repo.guardarResponsable(new Responsable("RESP-02", "Carlos Ruiz", "DIRECCION", 0, 5));
        repo.guardarResponsable(new Responsable("RESP-03", "Marta Díaz", "EXAMENES", 0, 3));

        System.out.println("--- 1. PRUEBA DE REGISTRO DE SOLICITUDES ---");
        
        RegistroSolicitudRequest req1 = new RegistroSolicitudRequest(
                "LEG-101", "Juan", "Pérez", "juan.perez@utn.edu.ar", "42111222",
                24, true, "CERTIFICADO", "Solicito certificado de alumno regular para presentar en el trabajo."
        );
        System.out.println(gestor.registrarSolicitud(req1));

        RegistroSolicitudRequest req2 = new RegistroSolicitudRequest(
                "LEG-102", "Ana", "López", "ana.lopez@utn.edu.ar", "43222333",
                12, true, "EQUIVALENCIA", "Solicito equivalencia de Análisis Matemático I cursada en otra facultad."
        );
        System.out.println(gestor.registrarSolicitud(req2));

        RegistroSolicitudRequest req3 = new RegistroSolicitudRequest(
                "LEG-103", "Pedro", "Sánchez", "pedro.sanchez@utn.edu.ar", "44333444",
                18, true, "EXCEPCION", "Pedido de excepción para rendir final sin correlativa inmediata."
        );
        System.out.println(gestor.registrarSolicitud(req3));

        System.out.println("\n--- 2. PRUEBA DE ASIGNACIÓN DE RESPONSABLES ---");
        System.out.println(gestor.asignarResponsable(1001, "RESP-01")); // OK: Certificado -> ALUMNOS
        System.out.println(gestor.asignarResponsable(1002, "RESP-01")); // ERROR esperado: Equivalencia no va a ALUMNOS
        System.out.println(gestor.asignarResponsable(1002, "RESP-02")); // OK: Equivalencia -> DIRECCION

        System.out.println("\n--- 3. PRUEBA DE CAMBIO DE ESTADO ---");
        System.out.println(gestor.cambiarEstadoSolicitud(1001, "APROBADA", "Certificado emitido y firmado digitalmente."));
        System.out.println(gestor.cambiarEstadoSolicitud(1002, "RECHAZADA", "Falta programa legalizado de la universidad de origen."));

        System.out.println("\n--- 4. PRUEBA DE BÚSQUEDA Y FILTRADO ---");
        List<Solicitud> urgentes = gestor.buscarSolicitudes(null, null, 1);
        System.out.println("Solicitudes con prioridad Alta (1) encontradas: " + urgentes.size());
        for (Solicitud s : urgentes) {
            System.out.println(" -> ID: " + s.getId() + " | Tipo: " + s.getTipo() + " | Estado: " + s.getEstado() + " | Alumno: " + s.getEstudiante().getNombre() + " " + s.getEstudiante().getApellido());
        }

        System.out.println("\n--- 5. REPORTE GENERAL ---");
        System.out.println(gestor.generarReporteGeneral());
    }
}