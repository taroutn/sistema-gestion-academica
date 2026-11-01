# 📋 Code Review y Criterios de Aceptación Técnica (TAC)

**Revisor:** Lautaro Ariel Cejas
**Resolución:** ✅ APROBADO CON OBSERVACIONES MENORES

| Categoría | Ítem de Verificación (Checklist) | Estado | Observaciones y Feedback Técnico |
| :--- | :--- | :---: | :--- |
| **Compilación y Ejecución** | ¿El proyecto compila sin errores ni *warnings* y supera todas las pruebas de `Main.java`? | **Cumple** | Ejecución limpia. Se corrigió exitosamente el bug del contador de reportes en la versión refactorizada. |
| **Encapsulamiento y Dominio** | ¿Se eliminaron los atributos públicos y la obsesión por primitivos? | **Cumple** | Atributos pasados a `private` y creación de `EstadoSolicitud`, `TipoSolicitud` y `Prioridad` como *Enums*. |
| **Responsabilidad (SOLID)** | ¿Se desacopló la persistencia de la lógica de negocio? | **Cumple** | Se aplicaron SRP y DIP extrayendo el almacenamiento hacia `SolicitudRepository`. |
| **Cohesión (Clean Code)** | ¿Se erradicaron los métodos extensos y listas largas de parámetros? | **Cumple** | `registrarSolicitud()` se redujo a 20 líneas y su firma a 1 objeto (`RegistroSolicitudRequest`). *Mejora futura:* Reemplazar el `int[]` de `calcularPrioridadYDias` por polimorfismo. |