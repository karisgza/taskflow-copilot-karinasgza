# Arquitectura de TaskFlow

Esta guía rápida está pensada para un desarrollador nuevo en el proyecto TaskFlow. Explica las capas, paquetes, el recorrido completo de `POST /projects/{projectId}/tasks`, dónde viven las reglas de negocio, cómo funciona la seguridad con JWT y cómo están organizados los tests.

## Capas y paquetes

La aplicación sigue una arquitectura en capas clásica (Controller → Service → Repository → Model) con DTOs y mappers:

- Controladores (HTTP): `src/main/java/com/taskflow/controller`
  - Clases típicas: `ProjectController` (`src/main/java/com/taskflow/controller/ProjectController.java`), `TaskController` (`src/main/java/com/taskflow/controller/TaskController.java`), `AuthController` (`src/main/java/com/taskflow/controller/AuthController.java`).
  - Responsabilidad: recibir peticiones, validar DTOs (`@Valid`), devolver respuestas (200/201/204) y cabeceras (`Location` en POST que crea).

- Servicios (casos de uso): `src/main/java/com/taskflow/service`
  - Clases típicas: `ProjectService` (`src/main/java/com/taskflow/service/ProjectService.java`), `TaskService` (`src/main/java/com/taskflow/service/TaskService.java`), `AuthService` (`src/main/java/com/taskflow/service/AuthService.java`).
  - Responsabilidad: orquestar transacciones, aplicar reglas que requieren coordinación (por ejemplo, comprobar permisos, persistir varias entidades, lanzar eventos).

- Repositorios (persistencia): `src/main/java/com/taskflow/repository`
  - Clases típicas: `ProjectRepository` (`src/main/java/com/taskflow/repository/ProjectRepository.java`), `TaskRepository` (`src/main/java/com/taskflow/repository/TaskRepository.java`), `UserRepository` (`src/main/java/com/taskflow/repository/UserRepository.java`).
  - Basados en Spring Data JPA; métodos `save`, `findById`, queries específicas.

- Modelo y lógica de dominio (entidades): `src/main/java/com/taskflow/model`
  - Clases: `Task` (`src/main/java/com/taskflow/model/Task.java`), `Project` (`src/main/java/com/taskflow/model/Project.java`), `User` (`src/main/java/com/taskflow/model/User.java`).
  - Aquí viven las invariantes y reglas de negocio relacionadas con una entidad: por ejemplo `Task.crear(...)`, `Task.estaVencida()`, y las transiciones de estado.

- DTOs y mappers: `src/main/java/com/taskflow/dto` y `src/main/java/com/taskflow/mapper`
  - Archivos: `TaskRequest`/`TaskResponse` (`src/main/java/com/taskflow/dto/TaskRequest.java`, `src/main/java/com/taskflow/dto/TaskResponse.java`), `TaskMapper` (`src/main/java/com/taskflow/mapper/TaskMapper.java`) con métodos como `TaskMapper.aResponse`.
  - Los controladores reciben/retornan DTOs, no entidades.

- Seguridad y configuración: `src/main/java/com/taskflow/security` o `src/main/java/com/taskflow/config`
  - Clases: `SecurityConfig` (`src/main/java/com/taskflow/config/SecurityConfig.java`), `JwtAuthenticationFilter`/`JwtService` (`src/main/java/com/taskflow/security/JwtAuthenticationFilter.java`, `src/main/java/com/taskflow/security/JwtService.java`), `ProjectSecurity` (`src/main/java/com/taskflow/security/ProjectSecurity.java`).

- Otros: `DataSeeder` (`src/main/java/com/taskflow/config/DataSeeder.java`) (datos iniciales para perfil `h2`), `GlobalExceptionHandler` (`src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`).

## Recorrido de `POST /projects/{projectId}/tasks`

1. Petición HTTP: `POST /projects/{projectId}/tasks` llega a `TaskController.createTask` (`src/main/java/com/taskflow/controller/TaskController.java`) con un body `TaskRequest`.
2. Comprobación de proyecto: `TaskController` invoca `projectService.buscarPorId(projectId)` (`src/main/java/com/taskflow/service/ProjectService.java`) y lanza `ProjectNotFoundException` (404) si no existe.
3. Llamada al servicio: `TaskController` llama a `taskService.crear(request, projectId)` (`src/main/java/com/taskflow/service/TaskService.java`).
   - Dentro de `TaskService.crear`, el `TaskMapper.aEntidadNueva(request, projectId)` (`src/main/java/com/taskflow/mapper/TaskMapper.java`) convierte el DTO en una entidad nueva (que pasa por `Task.crear` para validar `dueDate`) y luego `TaskRepository.save(...)` (`src/main/java/com/taskflow/repository/TaskRepository.java`) persiste la entidad.
4. Respuesta HTTP: `TaskController` construye la URI del recurso y responde `201 Created` con cabecera `Location` y el body producido por `TaskMapper.aResponse(creada)` (`src/main/java/com/taskflow/mapper/TaskMapper.java`).

Errores y códigos: proyecto no encontrado → `404 Not Found`; violación de reglas de negocio → `422 Unprocessable Entity`; validación del DTO → `400 Bad Request`.

## Dónde viven las reglas de negocio

- Reglas por entidad e invariantes que siempre deben cumplirse: dentro de la entidad (`Task`) en métodos como `Task.crear(...)`, `Task.setStatus(...)`, `Task.estaVencida()` — reutilizar estos métodos en servicios.
- Reglas que coordinan varios objetos o requieren transacciones (por ejemplo: reasignar responsable y notificar): dentro de `TaskService` o `ProjectService` (`src/main/java/com/taskflow/service`).
- No duplicar reglas: los controladores no deben contener lógica de negocio; solo orquestación y validación de entrada.

## Seguridad con JWT

- Autenticación: `POST /auth/login` (`src/main/java/com/taskflow/controller/AuthController.java`) llama a `AuthService` para validar credenciales y generar un token JWT con `JwtService` (`src/main/java/com/taskflow/security/JwtService.java`).
- Stateless: no hay sesión en servidor. El token contiene la información necesaria y se envía en `Authorization: Bearer <token>` en cada petición.
- Filtro JWT: `JwtAuthenticationFilter` (`src/main/java/com/taskflow/security/JwtAuthenticationFilter.java`) intercepta peticiones, valida el token, extrae el `username` y roles, y carga un `Authentication` en el `SecurityContext`. 
- Rutas públicas: `/auth/**`, `/info`, swagger (documentación), consola H2 y recursos estáticos (`src/main/resources/static`) son públicas según configuración en `SecurityConfig` (`src/main/java/com/taskflow/config/SecurityConfig.java`).
- Autorización: métodos sensibles usan `@PreAuthorize` y, cuando se necesita comprobación fina (por ejemplo borrar un proyecto solo por su dueño o un `ADMIN`), `ProjectSecurity` (`src/main/java/com/taskflow/security/ProjectSecurity.java`) provee la lógica y se referencia en la anotación.

## Organización de tests

- Unidad (rápidos, sin Spring): `src/test/java/...` con sufijo `*Test.java` (JUnit 5 + Mockito). Prueban lógica pura y métodos de entidad/servicio aislados.
- Slice / pruebas de capa: `@WebMvcTest`, `@DataJpaTest` en `src/test/java/...` para probar controladores/repositories con contexto reducido.
- Integración: `@SpringBootTest` en `src/test/java/...` con perfil `test`. Ejecutan la aplicación y pruebas end-to-end en memoria.
- Tests de integración más pesados (Testcontainers) se encuentran como `*IT.java` y están desactivados por defecto: requieren `-Ddocker.tests=true` para correr.

Comandos útiles:

- Ejecutar suite rápida: `mvn -q test`
- Ejecutar una sola clase de test: `mvn -q test "-Dtest=TaskServiceTest"`
- Ejecutar la app con H2 y datos de ejemplo: `mvn spring-boot:run "-Dspring-boot.run.profiles=h2"` (usa `DataSeeder`).

## Convenciones importantes

- DTOs como `record` con validación (Bean Validation). Controladores usan `@Valid`.
- Inyección por constructor. Sin Lombok.
- `POST` que crea → `201 Created` + `Location`.
- `DELETE` → `204 No Content`.
- No exponer entidades desde los controladores: siempre DTOs y mappers.
- Errores convertidos por `GlobalExceptionHandler` (`src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`) en respuestas consistentes (400, 404, 422, 409, 401, 403).

## Dónde mirar primero cuando surja un bug en creación de tareas

1. `TaskController` (`src/main/java/com/taskflow/controller/TaskController.java`) — validación y mapping del request.
2. `TaskService` (`src/main/java/com/taskflow/service/TaskService.java`) — orquestación y permisos.
3. `Task` (`src/main/java/com/taskflow/model/Task.java`) — invariantes y reglas de negocio.
4. `TaskRepository` (`src/main/java/com/taskflow/repository/TaskRepository.java`) — queries y persistencia.
5. `TaskMapper` (`src/main/java/com/taskflow/mapper/TaskMapper.java`) — transformación a DTO.

---

Si se desea, se pueden añadir diagramas o ejemplos de requests/responses. Para cambios en el código, seguir las reglas de commits y ejecutar `mvn -q test` tras modificarlos.
Las fechas límite se validan en `Task.crear` (`src/main/java/com/taskflow/model/Task.java`).
