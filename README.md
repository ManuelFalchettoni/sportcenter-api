# sportcenter-api

Sistema de turnos para centro deportivo — Spring Boot REST API.

## Stack

- Java 21
- Spring Boot 4.0.6 (Web, Data JPA, Security, Validation)
- MySQL
- Maven

## Cómo correr

> Requiere una instancia de MySQL corriendo y configurada en `src/main/resources/application.properties`.

```bash
cd sportcenter-api
./mvnw spring-boot:run
```

La aplicación queda escuchando por defecto en `http://localhost:8080`.

---

## Autenticación

La API usa **JWT (HS256)**. El flujo típico es:

1. Registrarse con `POST /sportcenter/users` (público).
2. Hacer login en `POST /sportcenter/auth/login` para obtener un token.
3. Mandar el token en cada request protegido en el header `Authorization: Bearer <token>`.

#### `POST /sportcenter/auth/login`

Body:

```json
{
  "email": "manu@example.com",
  "password": "secret123"
}
```

Respuesta `200 OK`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Si el email no existe **o** la contraseña es incorrecta, responde `401 Unauthorized` con el mismo mensaje genérico (`"Invalid email or password."`). Es deliberado: no se revela si un email está registrado, para evitar la enumeración de usuarios.

#### Rutas públicas vs. protegidas

- **Públicas:** `/sportcenter/auth/**` (login) y el registro `POST /sportcenter/users`.
- **Protegidas:** cualquier otra ruta requiere un token válido; sin él se responde `401 Unauthorized`.
- **Solo ADMIN** (`@PreAuthorize("hasRole('ADMIN')")` — un autenticado sin ese rol recibe `403 Forbidden`):
  - Gestión de usuarios: listar, actualizar, borrar y cambiar rol. Ver un usuario por id permite además al **propio usuario** (`hasRole('ADMIN') or #id == principal.id`).
  - Escrituras de profesionales y de tipos de servicio (`POST`/`PUT`/`DELETE`). Las lecturas quedan abiertas a cualquier autenticado, porque un `USER` las necesita para reservar turnos.

Cada tabla de endpoints indica el permiso requerido en la columna **Acceso**.

#### Cómo se autentica cada request (JWT + UserDetails)

El token lleva el `username` como subject y expira según `jwt.expiration` (1 hora por defecto). Las sesiones son *stateless*: no se guarda estado en el servidor.

El token **solo prueba identidad**. En cada request, `JwtFilter` valida la firma, extrae el username y carga el usuario real desde la base (`CustomUserDetailsService` → `UserPrincipal`, el adaptador `UserDetails` que envuelve la entidad). El rol con el que se autoriza sale siempre de la DB, no del token; el claim `role` que viaja en el JWT es solo informativo para el cliente. Consecuencias:

- Un usuario **borrado** pierde acceso al instante, aunque su token siga vigente (`401`).
- Un **cambio de rol** (`PATCH /users/{id}/role`) tiene efecto inmediato, sin esperar a que expire el token.
- Los controllers pueden recibir el usuario completo con `@AuthenticationPrincipal UserPrincipal`.

---

## Entidades

### Professional

Profesional que presta servicios en el centro deportivo.

| Campo        | Tipo    | Restricciones          |
|--------------|---------|------------------------|
| `id`         | Long    | PK, autogenerado       |
| `name`       | String  | `@NotBlank`, `@Size(2..100)` |
| `speciality` | String  | `@NotBlank`, `@Size(3..50)`  |
| `active`     | Boolean | `@NotNull`             |
| `services`   | Set<ServiceType> | `@ManyToMany` — servicios que ofrece (tabla `professional_service_types`) |

#### Endpoints — base: `/sportcenter/professionals`

| Método | Path                  | Descripción                       | Acceso      | Respuesta                      |
|--------|-----------------------|-----------------------------------|-------------|--------------------------------|
| GET    | `/{id}`               | Obtiene un profesional por id     | Autenticado | `200 OK` · `ProfessionalResponse` |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | Autenticado | `200 OK` · `Page<ProfessionalResponse>` |
| POST   | `/`                   | Crea un profesional               | ADMIN       | `201 Created` + header `Location` |
| PUT    | `/{id}`               | Actualiza un profesional          | ADMIN       | `200 OK` · `ProfessionalResponse` |
| DELETE | `/{id}`               | Elimina un profesional            | ADMIN       | `204 No Content`              |

##### Body de ejemplo (`POST` / `PUT`)

```json
{
  "name": "Juan Pérez",
  "speciality": "Kinesiología",
  "active": true,
  "serviceTypeIds": [1, 2]
}
```

El campo `serviceTypeIds` es opcional. Cada id debe ser un Long positivo no nulo (`@Positive`, `@NotNull`) y existir en `ServiceType` (sino `404 Not Found`). Se permite un máximo de 50 ids por request. En el `Response` los servicios se devuelven como `Set<ServiceTypeResponse>` bajo el campo `services`.

---

### ServiceType

Tipo de servicio que ofrece el centro (ej: sesión de kinesiología, clase de yoga, etc.).

| Campo             | Tipo       | Restricciones                |
|-------------------|------------|------------------------------|
| `id`              | Long       | PK, autogenerado             |
| `name`            | String     | `@NotBlank`, `@Size(3..80)`  |
| `durationMinutes` | Integer    | `@NotNull`, `@Positive`, `@Max(480)` |
| `price`           | BigDecimal | `@NotNull`, `@PositiveOrZero`, `@Digits(integer=8, fraction=2)` — columna `DECIMAL(10,2)` |

#### Endpoints — base: `/sportcenter/service-types`

| Método | Path                  | Descripción                       | Acceso      | Respuesta                      |
|--------|-----------------------|-----------------------------------|-------------|--------------------------------|
| GET    | `/{id}`               | Obtiene un service type por id    | Autenticado | `200 OK` · `ServiceTypeResponse` |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | Autenticado | `200 OK` · `Page<ServiceTypeResponse>` |
| POST   | `/`                   | Crea un service type              | ADMIN       | `201 Created` + header `Location` |
| PUT    | `/{id}`               | Actualiza un service type         | ADMIN       | `200 OK` · `ServiceTypeResponse` |
| DELETE | `/{id}`               | Elimina un service type           | ADMIN       | `204 No Content`              |

##### Body de ejemplo (`POST` / `PUT`)

```json
{
  "name": "Sesión de kinesiología",
  "durationMinutes": 45,
  "price": 8500.00
}
```

---

### User

Usuario del sistema. La contraseña se almacena hasheada con BCrypt y nunca se devuelve en las respuestas.

| Campo         | Tipo          | Restricciones                                  |
|---------------|---------------|------------------------------------------------|
| `id`          | Long          | PK, autogenerado                               |
| `username`    | String        | `@NotBlank`, `@Size(3..30)`, `@Pattern` (letras, dígitos, `.`, `_`, `-`), único |
| `email`       | String        | `@NotBlank`, `@Email`, `@Size(max=254)`, único |
| `password`    | String        | 8–72 chars; se guarda hasheado con BCrypt (60 chars) |
| `role`        | UserEnum      | `ADMIN` o `USER` — fijado por el servidor, no aceptado en el body |
| `createdDate` | LocalDateTime | Generado por el servidor, no editable          |

#### Endpoints — base: `/sportcenter/users`

| Método | Path                  | Descripción                       | Acceso              | Respuesta                              |
|--------|-----------------------|-----------------------------------|---------------------|----------------------------------------|
| GET    | `/{id}`               | Obtiene un usuario por id         | ADMIN o el propio usuario | `200 OK` · `UserResponse`        |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | ADMIN               | `200 OK` · `Page<UserResponse>`        |
| POST   | `/`                   | Crea un usuario (registro)        | Público             | `201 Created` · `UserResponse`         |
| PUT    | `/{id}`               | Actualiza un usuario              | ADMIN               | `200 OK` · `UserResponse`              |
| PATCH  | `/{id}/role`          | Cambia el rol                     | ADMIN               | `200 OK` · `UserResponse`              |
| DELETE | `/{id}`               | Elimina un usuario                | ADMIN               | `204 No Content`                       |

Si `username` o `email` ya existen, responde `409 Conflict` (`UserAlreadyExistsException`).

##### Body de ejemplo (`POST` / `PUT`)

```json
{
  "username": "manu",
  "email": "manu@example.com",
  "password": "secret123"
}
```

Notas:

- `role` **no se acepta en el body** del `POST` ni del `PUT`. Todo usuario creado por esos endpoints queda con rol `USER`. El rol se modifica exclusivamente vía `PATCH /sportcenter/users/{id}/role` (ver más abajo), restringido a administradores.
- `email` se normaliza a minúsculas y `username` se trimea antes de comparar y persistir, así la unicidad no depende de capitalización ni espacios accidentales.
- `password` debe tener entre 8 y 72 caracteres (límite superior por BCrypt, que trunca silenciosamente más allá de 72 bytes). En el `POST` es obligatoria. En el `PUT` se puede **omitir el campo** (enviarlo como `null` o no incluirlo) para no cambiar la clave; si se envía, debe respetar el rango 8–72.

##### Body de ejemplo (`PATCH /{id}/role`)

```json
{
  "role": "ADMIN"
}
```

Validaciones:

- `role` es obligatorio (`@NotNull`) y debe ser uno de los valores del enum (`USER`, `ADMIN`).
- **Endpoint restringido a administradores.** Protegido con `@PreAuthorize("hasRole('ADMIN')")` (method-level security): un usuario autenticado sin rol `ADMIN` recibe `403 Forbidden`, y sin token `401 Unauthorized`.
- El cambio de rol tiene **efecto inmediato**: como el rol se carga desde la base en cada request (ver sección de autenticación), el usuario afectado no necesita reloguearse.
- `createdDate` se setea automáticamente en el `POST` y no puede modificarse.

---

### Appointment

Turno reservado entre un usuario y un profesional para un tipo de servicio determinado.

| Campo          | Tipo          | Restricciones                                                |
|----------------|---------------|--------------------------------------------------------------|
| `id`           | Long          | PK, autogenerado                                             |
| `startTime`    | LocalDateTime | `@NotNull`, `@Future`                                        |
| `endTime`      | LocalDateTime | `@NotNull`, `@Future` — debe ser posterior a `startTime`     |
| `status`       | AppointmentStatusEnum | `@NotNull`, `PENDING` \| `CONFIRMED` \| `CANCELLED`. Se inicializa en `PENDING` |
| `statusModifiedAt` | LocalDateTime | Nullable. Se setea en cada cambio de estado (ej: al cancelar) |
| `notes`        | String        | Opcional, `@Size(max = 500)`                                 |
| `createdAt`    | LocalDateTime | Generado por el servidor, no editable                        |
| `user`         | User          | `@ManyToOne`, requerido — siempre el usuario autenticado que creó el turno |
| `professional` | Professional  | `@ManyToOne`, requerido — `professionalId` `@NotNull` `@Positive` |
| `serviceType`  | ServiceType   | `@ManyToOne`, requerido — `serviceTypeId` `@NotNull` `@Positive`  |

#### Endpoints — base: `/sportcenter/appointments`

| Método | Path                  | Descripción                       | Acceso          | Respuesta                              |
|--------|-----------------------|-----------------------------------|-----------------|----------------------------------------|
| GET    | `/{id}`               | Obtiene un turno por id           | Dueño o ADMIN   | `200 OK` · `AppointmentResponse`       |
| GET    | `?page=&size=&sort=`  | Lista paginada (ADMIN ve todos; USER solo los suyos) | Autenticado | `200 OK` · `Page<AppointmentResponse>` |
| POST   | `/`                   | Crea un turno a nombre del autenticado | Autenticado | `201 Created` · `AppointmentResponse`  |
| PUT    | `/{id}`               | Actualiza un turno                | Dueño o ADMIN   | `200 OK` · `AppointmentResponse`       |
| PATCH  | `/{id}/cancel`        | Cancela un turno (soft delete)    | Dueño o ADMIN   | `200 OK` · `AppointmentResponse`       |
| DELETE | `/{id}`               | Elimina un turno                  | Dueño o ADMIN   | `204 No Content`                       |

##### Ownership

El dueño de un turno es **siempre el usuario autenticado que lo creó**: el body no acepta `userId`, así nadie puede reservar a nombre de otro. Para operar sobre un turno existente, `AppointmentOwnershipValidator` exige ser el dueño o ADMIN; un tercero recibe `403 Forbidden`. El `PUT` no transfiere el turno: el dueño nunca cambia. En el listado, un `USER` ve únicamente sus turnos y un ADMIN ve todos.

##### Body de ejemplo (`POST` / `PUT`)

```json
{
  "startTime": "2026-07-10T10:00:00",
  "endTime": "2026-07-10T10:45:00",
  "notes": "Primera sesión",
  "professionalId": 2,
  "serviceTypeId": 3
}
```

Reglas:

- `endTime` debe ser estrictamente posterior a `startTime`; caso contrario responde `400 Bad Request`.
- `professionalId` y `serviceTypeId` deben referenciar entidades existentes; caso contrario `404 Not Found`.
- **Sin doble reserva del profesional:** un profesional no puede tener dos turnos activos que se solapen. Si el rango pedido pisa otro turno del mismo profesional, responde `409 Conflict` (`AppointmentOverlapException`).
- **Sin doble reserva del usuario:** un usuario tampoco puede tener dos turnos activos a la misma hora, **aunque sean con profesionales distintos** (una persona no puede estar en dos lugares a la vez). Responde `409 Conflict` (`UserAppointmentOverlapException`). En el `PUT` se valida contra el dueño del turno, no contra el caller: un ADMIN editando un turno ajeno no compromete su propia agenda.
- En ambos casos, en el `PUT` el propio turno que se edita no cuenta como solapamiento.
- En el `POST`, el turno se crea con `status = PENDING` y `createdAt` se setea al momento actual.

##### Cancelación (soft delete)

`PATCH /sportcenter/appointments/{id}/cancel` marca el turno como cancelado sin eliminarlo:

- Setea `status = CANCELLED` y `statusModifiedAt = now()`.
- El turno queda en el historial pero **deja de ocupar el horario** (ni del profesional ni del usuario): el chequeo de solapamiento ignora los turnos cancelados (sufijo `AndStatusNot` en el repositorio, con `CANCELLED` como estado excluido), así que ese slot queda libre para reservarse de nuevo.
- Si el turno ya estaba cancelado, responde `409 Conflict` (`AppointmentAlreadyCancelledException`).
- Si el id no existe, responde `404 Not Found`.

A diferencia del `DELETE`, que borra el registro físicamente, la cancelación preserva la traza del turno (quién, cuándo se reservó y cuándo cambió de estado).

##### Cómo se detecta el solapamiento

La regla vive en `AppointmentOverlapValidator` (un `@Component`, mismo patrón que `AppointmentOwnershipValidator`): los services de creación y actualización lo invocan y él consulta el repositorio y lanza la excepción que corresponda. Valida los dos ejes — la agenda del profesional y la del usuario — con consultas espejo.

Las consultas están en `JpaAppointmentRepository`, que es una **interfaz** sin implementación escrita a mano:

```java
boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNot(
        Long professionalId, LocalDateTime endTime, LocalDateTime startTime,
        AppointmentStatusEnum excludedStatus);
```

No tiene cuerpo porque es un **derived query method** de Spring Data JPA: al arrancar, Spring genera un proxy que implementa la interfaz y, leyendo el **nombre** del método, arma la consulta. El nombre se descompone así:

| Fragmento del nombre | Condición generada    |
|----------------------|-----------------------|
| `ProfessionalId`     | `professional.id = ?` |
| `StartTimeBefore`    | `startTime < ?`       |
| `EndTimeAfter`       | `endTime > ?`         |
| `StatusNot`          | `status <> ?`         |

Que se traduce (en JPQL, simplificado) a:

```sql
SELECT count(a) > 0 FROM Appointment a
WHERE a.professional.id = :professionalId
  AND a.startTime < :endTime
  AND a.endTime   > :startTime
  AND a.status   <> :excludedStatus
```

Los parámetros se asignan **por posición**, en el orden en que aparecen en el nombre. Por eso en la llamada se pasan "cruzados": el `endTime` del turno nuevo se compara contra el `startTime` de los turnos existentes y viceversa. Dos intervalos se solapan cuando **uno empieza antes de que el otro termine y termina después de que el otro empieza**. Se usan comparaciones estrictas (`<`, `>`) a propósito: dos turnos contiguos (uno termina justo cuando el otro empieza, p. ej. `10–11` y `11–12`) **no** se consideran solapados. El filtro `status <> CANCELLED` deja afuera los turnos cancelados, así un horario cancelado vuelve a estar disponible.

Variantes:

- `existsByUserId...AndStatusNot` — la consulta espejo por usuario: detecta que el dueño ya tenga otro turno activo en ese rango, con cualquier profesional.
- `...AndIdNot...(..., id)` — agrega `AND a.id <> :id` y se usa al actualizar, para que un turno no choque consigo mismo.

> El SQL real generado se puede ver en consola gracias a `spring.jpa.show-sql=true`.

##### Limitación conocida: concurrencia

El patrón es *check-then-act*: primero se consulta si existe un turno solapado y después se guarda, sin atomicidad entre ambos pasos. Si dos requests piden el mismo horario **exactamente al mismo tiempo**, ambas pueden pasar el chequeo antes de que cualquiera persista, y se produce una doble reserva.
---

## Manejo de errores

Todas las respuestas de error siguen un formato JSON uniforme provisto por `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-06-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "endTime must be after startTime"
}
```

Para errores de validación (`@Valid` sobre el body), además se incluye un objeto `errors` con el detalle por campo:

```json
{
  "timestamp": "2026-06-05T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "must be a well-formed email address",
    "password": "must not be blank"
  }
}
```

Adicionalmente, Bean Validation está activado en los callbacks pre-persist y pre-update de Hibernate (`jakarta.persistence.validation.mode=auto`). Si alguna entidad inválida intenta persistirse por una vía que no pasó por `@Valid` en un controller (por ejemplo un seeder o job interno), se devuelve `400` con `message = "Entity validation failed"` y el mismo objeto `errors` mapeando cada propiedad violada a su mensaje.

### Códigos de estado

| Código | Cuándo |
|--------|--------|
| `400 Bad Request` | Validación del body (`@Valid`), JSON malformado, o reglas de negocio como `endTime <= startTime`. |
| `401 Unauthorized` | Falta el token, o es inválido/expirado; o credenciales inválidas en el login. |
| `403 Forbidden` | Autenticado pero sin permisos: un no-ADMIN en un endpoint de admin, o un usuario operando sobre un turno ajeno. |
| `404 Not Found` | Recurso inexistente (usuario, profesional, service type o turno). |
| `409 Conflict` | `username`/`email` duplicado, turno solapado (con otro turno del profesional o del propio usuario), o turno ya cancelado. |
| `500 Internal Server Error` | Error inesperado; el detalle se loguea en el servidor y **no** se expone al cliente. |

Todos comparten el mismo formato de body de arriba. `GlobalExceptionHandler` es la única fuente de verdad: por eso las excepciones de dominio no usan `@ResponseStatus`.

---

## Paginación

Los endpoints `GET` (listado) aceptan los query params estándar de Spring Data:

- `page` — número de página (0-indexed). Default `0`.
- `size` — cantidad de elementos por página. Default `20`.
- `sort` — campo y dirección. Ej: `sort=name,asc` o `sort=price,desc`.

Ejemplo:

```
GET /sportcenter/service-types?page=0&size=10&sort=price,desc
```

## Tests

La suite usa **JUnit 5 + Mockito**, sin necesidad de MySQL ni de levantar el contexto completo de Spring.

```bash
cd sportcenter-api
./mvnw test "-Dtest=*ServiceTest,*ControllerTest"
```

**Tests unitarios de servicios** (Mockito puro sobre la capa de lógica de negocio):

- `AppointmentCreatorServiceTest` / `AppointmentUpdaterServiceTest` — alta y actualización de turnos: rango horario inválido (`endTime <= startTime`), entidades inexistentes (professional/serviceType), que un solapamiento rechazado por el validador corte la operación, que el turno se cree a nombre del usuario autenticado y que un caller ajeno sea rechazado (**ownership**).
- `AppointmentOverlapValidatorTest` — la **detección de solapamiento** en sus dos ejes: turnos del profesional y turnos del usuario (con cualquier profesional), tanto al crear como al actualizar (donde el propio turno editado no cuenta como choque).
- `AppointmentFinderServiceTest` / `UserFinderServiceTest` — recuperación por id, `NotFoundException` y, en la variante con caller, el chequeo de ownership.
- `JwtServiceTest` — generación y validación de tokens: token recién emitido válido, extracción del username (subject), rechazo de tokens basura, expirados o firmados con otra clave.
- `LoginServiceTest` — login OK, normalización del email, `401` con credenciales inválidas y la **mitigación de timing** (verificación contra el hash señuelo cuando el email no existe).
- `UserCreatorServiceTest` — normalización (trim/lowercase), hasheo de password, rol forzado a `USER` y `409` por username/email duplicado.
- `UserRoleUpdaterServiceTest` — cambio de rol y persistencia.

**Tests de integración de controllers** (`@WebMvcTest` + `MockMvc`, servicios mockeados): verifican routing, validación `@Valid` y que `GlobalExceptionHandler` traduzca cada caso al status y body correctos.

- `AuthLoginControllerTest` — `200` con token, `401` credenciales inválidas, `400` por validación del body.
- `UserPostControllerTest` — `201` (y que la respuesta **nunca expone el password**), `409` duplicado, `400` por validación.
- `AppointmentPostControllerTest` — `201`, `404` entidad inexistente, `409` solapamiento, `400` por rango/fecha (`@Future`) o campos faltantes.

> El test `contextLoads()` (`@SpringBootTest`) sí requiere una base de datos disponible, por eso queda fuera del filtro de arriba.

## Estructura del proyecto

Cada entidad sigue la misma organización por carpetas:

```
src/main/java/com/tpfinal/sportcenter_api/
├── config/                     # Seguridad: SecurityConfig, JwtService, JwtFilter,
│                               #   UserPrincipal, JwtAuthenticationEntryPoint
├── entity/<entidad>/           # Entidad JPA
├── dto/
│   ├── request/<entidad>/      # Request DTO
│   └── response/<entidad>/     # Response DTO
├── repository/<entidad>/       # Repositorio JPA
├── exception/<entidad>/        # Excepciones específicas
├── service/<entidad>/          # Servicios (Finder, GetAll, Creator, Updater, Deleter)
└── controller/<entidad>/       # Controllers (uno por verbo HTTP)
```
