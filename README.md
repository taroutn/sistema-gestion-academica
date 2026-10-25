#  TPI - Metodología de Sistemas II

## Caso: Sistema de Gestión de Solicitudes Académicas

## Descripción del sistema base
Este proyecto es el sistema base seleccionado para el Trabajo Práctico Integrador (TPI). Permite registrar solicitudes académicas de estudiantes, clasificarlas, asignar un responsable del área correspondiente y gestionar el cambio de estados hasta su resolución.

Actualmente, el proyecto representa el hito `estado-inicial`, donde el sistema funciona correctamente desde la mirada del usuario (calidad externa), pero presenta intencionalmente múltiples oportunidades de mejora en su estructura interna (deuda técnica y code smells).

## ⚙️ Tecnologías utilizadas
- **Lenguaje:** Java
- **Persistencia:** Memoria estructurada (Listas estáticas)
- **Entorno sugerido:** IntelliJ IDEA / Terminal nativa

## 🚀 Funcionalidades principales
El sistema cuenta con las siguientes operaciones funcionales verificables:
1. **Registrar solicitud:** Creación de trámites (Certificados, Equivalencias, Excepciones, Bajas).
2. **Asignar responsable:** Asignación basada en el área correspondiente y límite de carga del personal.
3. **Cambiar estado:** Transición del trámite (PENDIENTE -> EN_REVISION -> APROBADA/RECHAZADA).
4. **Búsqueda y filtrado:** Consulta de solicitudes por estado, tipo o prioridad.
5. **Generar reporte:** Salida por consola de estadísticas generales y log de auditoría.

## 🛠️ Instalación y uso

Para clonar y ejecutar el proyecto localmente desde la terminal:

```bash
git clone https://github.com/taroutn/sistema-gestion-academica.git
cd sistema-gestion-academica/src
javac sga/Main.java
java sga.Main
````
(Si utilizás IntelliJ IDEA, basta con abrir la carpeta del proyecto y ejecutar el archivo Main.java con el botón de Run).