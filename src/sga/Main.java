package sga;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        GestorSolicitudes gestor = new GestorSolicitudes();

        // Precarga de responsables en memoria
        GestorSolicitudes.baseDeDatosResponsables.add(
                new Responsable("RESP-01", "Laura Gómez", "ALUMNOS", 0, 2)
        );
        GestorSolicitudes.baseDeDatosResponsables.add(
                new Responsable("RESP-02", "Carlos Ruiz", "DIRECCION", 0, 5)
        );
        GestorSolicitudes.baseDeDatosResponsables.add(
                new Responsable("RESP-03", "Marta Díaz", "EXAMENES", 0, 3)
        );

        System.out.println("--- 1. PRUEBA DE REGISTRO DE SOLICITUDES ---");
        String r1 = gestor.registrarSolicitud(
                "LEG-101", "Juan", "Pérez", "juan.perez@utn.edu.ar", "42111222",
                24, true, "CERTIFICADO", "Solicito certificado de alumno regular para presentar en el trabajo."
        );
        System.out.println(r1);

        String r2 = gestor.registrarSolicitud(
                "LEG-102", "Ana", "López", "ana.lopez@utn.edu.ar", "43222333",
                12, true, "EQUIVALENCIA", "Solicito equivalencia de Análisis Matemático I cursada en otra facultad."
        );
        System.out.println(r2);

        String r3 = gestor.registrarSolicitud(
                "LEG-103", "Pedro", "Sánchez", "pedro.sanchez@utn.edu.ar", "44333444",
                18, true, "EXCEPCION", "Pedido de excepción para rendir final sin correlativa inmediata."
        );
        System.out.println(r3);

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
            System.out.println(" -> ID: " + s.id + " | Tipo: " + s.tipo + " | Estado: " + s.estado + " | Alumno: " + s.estudiante.nombre + " " + s.estudiante.apellido);
        }

        System.out.println("\n--- 5. REPORTE GENERAL ---");
        System.out.println(gestor.generarReporteGeneral());
    }
}