# Diagnóstico del sistema base

**Autor:** Manuel Galarza  
**Alcance:** calidad externa e interna, deuda técnica y mantenibilidad.  
**Versión analizada:** sistema base previo al refactoring.

El catálogo detallado de code smells y la documentación de las mejoras de diseño se desarrollan por separado como parte del trabajo del equipo.

## 1. Calidad externa vs. calidad interna

La calidad externa se relaciona con el comportamiento observable del sistema y su capacidad para responder a las necesidades del usuario. La calidad interna comprende las características del código que facilitan su comprensión, mantenimiento y evolución.

En el sistema analizado, las operaciones demostradas funcionan, pero su implementación contiene problemas de organización y distribución de responsabilidades.

### 1.1. Calidad externa

La ejecución de Main.java permitió verificar:

- Registro de tres solicitudes académicas.
- Asignación de responsables según el área.
- Rechazo de una asignación de equivalencia al área ALUMNOS.
- Aprobación de una solicitud y rechazo de otra.
- Búsqueda de solicitudes por prioridad alta.
- Generación de un reporte general.

El resultado fue consistente con las operaciones realizadas:

| Resultado | Cantidad |
|---|---:|
| Solicitudes registradas | 3 |
| Pendientes | 1 |
| En revisión | 0 |
| Aprobadas | 1 |
| Rechazadas | 1 |
| Solicitudes de prioridad alta | 2 |
| Eventos de auditoría | 7 |

Estas evidencias muestran un funcionamiento correcto en los escenarios incluidos en la demostración. No constituyen una verificación exhaustiva de todas las entradas ni de todos los casos límite.

### 1.2. Calidad interna

GestorSolicitudes concentra validaciones, reglas de negocio, almacenamiento en memoria, auditoría y generación de reportes.

El método registrarSolicitud() recibe nueve parámetros y reúne validación, creación de objetos, cálculo de prioridad y plazo, generación de identificadores, almacenamiento y registro de auditoría. Esta combinación dificulta comprender y modificar cada responsabilidad por separado.

También se repiten validaciones y búsquedas. Las entidades Estudiante, Responsable y Solicitud exponen sus atributos públicamente, mientras que el gestor modifica directamente el estado de las solicitudes y la carga de los responsables.

| Aspecto | Evidencia del código | Implicación |
|---|---|---|
| Legibilidad | registrarSolicitud() reúne múltiples tareas. | Comprender la operación exige analizar responsabilidades diferentes. |
| Mantenibilidad | Las validaciones de legajo y email se repiten en registro y asignación. | Una modificación requiere actualizar ambas ubicaciones. |
| Encapsulamiento | El gestor modifica directamente estado y solicitudesActivas. | Las entidades no controlan todas las modificaciones de sus datos. |
| Evolución | Las reglas por tipo se distribuyen entre registro y asignación. | Incorporar un trámite exige revisar distintos bloques del gestor. |

El funcionamiento visible constituye el punto de partida del trabajo. La mejora de la calidad interna busca facilitar futuros cambios y reducir el riesgo de regresiones, conservando el comportamiento esperado mediante verificaciones antes y después del refactoring.

## 2. Deuda técnica y mantenibilidad

La deuda técnica representa el esfuerzo adicional que determinadas decisiones de diseño o implementación pueden trasladar al mantenimiento futuro.

En este prototipo, los problemas fueron incorporados deliberadamente para el análisis y el refactoring. El diagnóstico se apoya en características observables del código, sin atribuirlas a una historia de desarrollo no documentada.

### 2.1. Concentración de responsabilidades

El principal foco de deuda técnica es GestorSolicitudes. La clase administra reglas de negocio, validaciones, almacenamiento, auditoría y reportes.

Esta organización genera múltiples motivos de cambio sobre un mismo componente y dificulta modificar una responsabilidad de manera aislada.

### 2.2. Impacto sobre el mantenimiento

**Incorporación de trámites.** La prioridad y el plazo estimado se calculan mediante condicionales en registrarSolicitud(), mientras que la correspondencia entre tipo y área se controla en asignarResponsable(). Agregar un trámite exige localizar y actualizar reglas distribuidas entre ambos métodos.

**Duplicación.** Las validaciones de legajo y email se repiten en registro y asignación. Si cambia el criterio, deben actualizarse ambas ubicaciones para evitar comportamientos inconsistentes. La búsqueda de solicitudes por ID también se repite en asignación y cambio de estado.

**Falta de encapsulamiento.** Los atributos públicos permiten modificar directamente el estado de una solicitud o la carga de un responsable, sin pasar por operaciones que protejan su consistencia.

**Representación de valores de dominio.** Los estados y tipos se representan mediante cadenas, y las prioridades mediante números. Su significado y sus valores válidos deben interpretarse y controlarse en diferentes partes del código.

**Estado compartido y almacenamiento.** Las listas públicas y estáticas comparten datos entre todas las instancias del gestor y vinculan las operaciones con una estructura concreta. Esto dificulta aislar pruebas y sustituir el almacenamiento. La memoria es adecuada para el alcance del prototipo; la dificultad está en cómo se accede a ella y se distribuye su responsabilidad.

### 2.3. Criterio de mejora

La reducción de esta deuda debe priorizar:

- Separar responsabilidades.
- Proteger las reglas de dominio.
- Eliminar validaciones y búsquedas duplicadas.
- Simplificar los parámetros del registro.
- Representar explícitamente los valores válidos de estado, tipo y prioridad.

El beneficio esperado es que las modificaciones puedan realizarse en componentes más acotados y con menos dependencias. Debe respaldarse mediante la comparación del código inicial y final y las verificaciones funcionales correspondientes.

No se dispone de mediciones para cuantificar una reducción del tiempo o del costo de mantenimiento. Las propuestas de este diagnóstico no implican que los cambios ya hayan sido implementados o verificados.

## 3. Evidencias del funcionamiento inicial

- [Salida completa de la ejecución](evidencias/ejecucion-inicial.txt).
- [Captura de la ejecución](evidencias/ejecucion-inicial.png).

La ejecución corresponde a los escenarios definidos en Main.java. La captura también se incorpora al informe grupal en el apartado «Anexo A. Evidencia de funcionamiento del sistema base».