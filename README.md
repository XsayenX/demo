# 🏗️ Sistema de Gestión Administrativa - Constructora Panameño Garcia

![Estado](https://img.shields.io/badge/Estado-En_Desarrollo-success)
![Versión](https://img.shields.io/badge/Versi%C3%B3n-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=java&logoColor=white)

Plataforma web integral desarrollada para centralizar, automatizar y optimizar los procesos administrativos y operativos de la **Constructora Panameño Garcia**. Este proyecto es parte de la cátedra de **Diseño de Sistemas I (DSI115)** de la Universidad de El Salvador.

---

## 🛠️ Metodología de Trabajo: SCRUM

El desarrollo de este sistema se está llevando a cabo bajo el marco de trabajo ágil **SCRUM**, lo que nos ha permitido realizar entregas de valor continuas y adaptarnos a los cambios en los requerimientos.

### 👥 Conformación del Equipo (Scrum Team)
*   **Product Owner:** Katya Raquel Raymundo Pérez
*   **Scrum Master:** Gerson André Hernández Carballo
*   **Equipo de Desarrollo:** Carlos Daniel Mijango Ramírez y Kevin Alejandro Arias Alfaro

### 🔄 Dinámica de Trabajo
1.  **Product Backlog:** Se gestionó a través de **Jira Software**, dividiendo los requerimientos globales del sistema en 10 "Épicas" y desglosándolas en Historias de Usuario (HU) estimadas y priorizadas.
2.  **Sprints:** Desarrollo iterativo con ciclos definidos. En cada Sprint Planning se seleccionan las HUs que aporten mayor valor (ej. empezar por Seguridad y Trabajadores antes que Planillas).
3.  **Trazabilidad:** Cada commit en el repositorio hace referencia a la HU trabajada (Ej: `feat(HU-10): ...`), manteniendo un control exacto entre el código y los requerimientos del cliente.

---

## 🚀 Lo Realizado (Features Implementadas)

Hasta el momento, el sistema cuenta con las siguientes funcionalidades terminadas, probadas y en producción, cubriendo las primeras Épicas del Backlog:

### 🔐 Épica 1 y 10: Seguridad y Control de Accesos
*   **(HU-3)** **Login y Presentación:** Landing Page moderna (estilo *Glassmorphism* y antigravedad) con inicio de sesión seguro usando **Spring Security**.
*   **(HU-1, HU-2)** **Gestión de Usuarios:** CRUD completo mediante ventanas modales para gestionar cuentas del sistema.
*   **(HU-31)** **Control de Accesos (RBAC):** Restricción estricta de rutas e interfaces (Principio de Menor Privilegio) basado en 4 roles: `ADMINISTRADOR`, `JEFE`, `SUPERVISOR` y `CONTADORA`. 

### 👷‍♂️ Épica 2: Gestión de Trabajadores
*   **(HU-4, HU-5)** **Directorio de Personal:** Registro y edición de trabajadores con validación automática estricta de formato DUI y prevención de duplicados.
*   **Catálogo de Cargos:** Administración dinámica de los puestos de trabajo en la obra y asignación de tarifa salarial por hora ($).
*   **(HU-6)** **Búsqueda en Tiempo Real:** Buscador eficiente por nombre o número de documento.

### ⏱️ Épica 3 (Parte A): Control Operativo (Asistencias)
*   **(HU-7)** **Registro Diario Masivo:** Interfaz ágil para anotar la entrada y salida de todo el personal activo en una fecha específica.
*   **🔌 Importación vía Excel (Apache POI):** Capacidad para que el supervisor suba un archivo `.xlsx` y el sistema procese automáticamente las asistencias en la base de datos.
*   **(HU-8)** **Horas Extras:** Vinculación de horas extra (Diurnas/Nocturnas) a jornadas específicas con su respectiva justificación.
*   **(HU-9)** **Auditoría de Jornadas:** Flujo de trabajo donde el `JEFE` aprueba o rechaza los reportes subidos por los supervisores.
*   **Cierre de Día:** Bloqueo de fechas finalizadas para mantener la integridad de los datos.

### 💰 Épica 3 (Parte B): Finanzas y Planillas
*   **(HU-10)** **Generación Automática:** Motor de cálculo que consolida todas las horas base y extras en estado "APROBADO" de un rango de fechas. Calcula el pago doble por extras nocturnas, aplica deducciones de ley (ISSS/AFP) y genera el salario neto.
*   **Cierre de Período:** Guardado definitivo de la nómina en la base de datos como comprobantes inmutables.
*   **(HU-11)** **Historial de Pagos:** Consulta de planillas cerradas y visualización en formato "Solo Lectura" listo para impresión y exportación a PDF.

### 🤝 Épica 4: Clientes y Proyectos (Fase Inicial)
*   **(HU-12)** **Directorio de Clientes:** Registro y mantenimiento de la cartera de clientes de la constructora con auto-formateador de DUI.

---

## 💻 Stack Tecnológico

El proyecto está construido sobre una arquitectura **MVC** sólida y escalable:

*   **Backend:** Java 17+, Spring Boot 3.2.5.
*   **Seguridad:** Spring Security (Cifrado BCrypt).
*   **Persistencia:** Spring Data JPA / Hibernate.
*   **Base de Datos:** MySQL 8.
*   **Frontend:** Thymeleaf, Bootstrap 5, HTML5, CSS3, Vanilla JS.
*   **Librerías Adicionales:** Apache POI (Procesamiento de Excel), Lombok.

---

## ⚙️ Instrucciones de Instalación y Ejecución

1. Clonar el repositorio.
2. Crear una base de datos en MySQL llamada `dsi115_db`.
3. Configurar las credenciales de la base de datos en `src/main/resources/application.properties`.
4. Ejecutar el proyecto mediante el IDE (IntelliJ IDEA, Eclipse, VSCode) o mediante Maven:
   ```bash
   ./mvnw spring-boot:run
