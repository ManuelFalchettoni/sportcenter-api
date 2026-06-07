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
- **Solo ADMIN:** `PATCH /sportcenter/users/{id}/role` exige rol `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")`); un usuario autenticado sin ese rol recibe `403 Forbidden`.

El token lleva el `username` como subject y el rol como claim, y expira según `jwt.expiration` (1 hora por defecto). Las sesiones son *stateless*: no se guarda estado en el servidor.

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

| Método | Path                  | Descripción                       | Respuesta                      |
|--------|-----------------------|-----------------------------------|--------------------------------|
| GET    | `/{id}`               | Obtiene un profesional por id     | `200 OK` · `ProfessionalResponse` |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | `200 OK` · `Page<ProfessionalResponse>` |
| POST   | `/`                   | Crea un profesional               | `201 Created` + header `Location` |
| PUT    | `/{id}`               | Actualiza un profesional          | `200 OK` · `ProfessionalResponse` |
| DELETE | `/{id}`               | Elimina un profesional            | `204 No Content`              |

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

| Método | Path                  | Descripción                       | Respuesta                      |
|--------|-----------------------|-----------------------------------|--------------------------------|
| GET    | `/{id}`               | Obtiene un service type por id    | `200 OK` · `ServiceTypeResponse` |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | `200 OK` · `Page<ServiceTypeResponse>` |
| POST   | `/`                   | Crea un service type              | `201 Created` + header `Location` |
| PUT    | `/{id}`               | Actualiza un service type         | `200 OK` · `ServiceTypeResponse` |
| DELETE | `/{id}`               | Elimina un service type           | `204 No Content`              |

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

| Método | Path                  | Descripción                       | Respuesta                              |
|--------|-----------------------|-----------------------------------|----------------------------------------|
| GET    | `/{id}`               | Obtiene un usuario por id         | `200 OK` · `UserResponse`              |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | `200 OK` · `Page<UserResponse>`        |
| POST   | `/`                   | Crea un usuario                   | `201 Created` · `UserResponse`         |
| PUT    | `/{id}`               | Actualiza un usuario              | `200 OK` · `UserResponse`              |
| PATCH  | `/{id}/role`          | **Admin**: cambia el rol          | `200 OK` · `UserResponse`              |
| DELETE | `/{id}`               | Elimina un usuario                | `204 No Content`                       |

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

- `role` **no se acepta en el body** del `POST` ni del `PUT`. Todo usuario creado por esos endpoints queda con rol `USER`. El rol se modifica exclusivamente vía `PATCH /sportcenter/users/{id}/role` (ver más abajo), que en producción debe quedar restringido a administradores.
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
- `createdDate` se setea automáticamente en el `POST` y no puede modificarse.

---

### Appointment

Turno reservado entre un usuario y un profesional para un tipo de servicio determinado.

| Campo          | Tipo          | Restricciones                                                |
|----------------|---------------|--------------------------------------------------------------|
| `id`           | Long          | PK, autogenerado                                             |
| `startTime`    | LocalDateTime | `@NotNull`, `@Future`                                        |
| `endTime`      | LocalDateTime | `@NotNull`, `@Future` — debe ser posterior a `startTime`     |
| `confirmed`    | Boolean       | Se inicializa en `false` al crear el turno                   |
| `notes`        | String        | Opcional, `@Size(max = 500)`                                 |
| `createdAt`    | LocalDateTime | Generado por el servidor, no editable                        |
| `user`         | User          | `@ManyToOne`, requerido — `userId` `@NotNull` `@Positive`         |
| `professional` | Professional  | `@ManyToOne`, requerido — `professionalId` `@NotNull` `@Positive` |
| `serviceType`  | ServiceType   | `@ManyToOne`, requerido — `serviceTypeId` `@NotNull` `@Positive`  |

#### Endpoints — base: `/sportcenter/appointments`

| Método | Path                  | Descripción                       | Respuesta                              |
|--------|-----------------------|-----------------------------------|----------------------------------------|
| GET    | `/{id}`               | Obtiene un turno por id           | `200 OK` · `AppointmentResponse`       |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | `200 OK` · `Page<AppointmentResponse>` |
| POST   | `/`                   | Crea un turno                     | `201 Created` · `AppointmentResponse`  |
| PUT    | `/{id}`               | Actualiza un turno                | `200 OK` · `AppointmentResponse`       |
| DELETE | `/{id}`               | Elimina un turno                  | `204 No Content`                       |

##### Body de ejemplo (`POST` / `PUT`)

```json
{
  "startTime": "2026-07-10T10:00:00",
  "endTime": "2026-07-10T10:45:00",
  "notes": "Primera sesión",
  "userId": 1,
  "professionalId": 2,
  "serviceTypeId": 3
}
```

Reglas:

- `endTime` debe ser estrictamente posterior a `startTime`; caso contrario responde `400 Bad Request`.
- `userId`, `professionalId` y `serviceTypeId` deben referenciar entidades existentes; caso contrario `404 Not Found`.
- **Sin doble reserva:** un profesional no puede tener dos turnos que se solapen. Si el rango pedido pisa otro turno del mismo profesional, responde `409 Conflict` (`AppointmentOverlapException`). En el `PUT`, el propio turno que se edita no cuenta como solapamiento.
- En el `POST`, el turno se crea con `confirmed = false` y `createdAt` se setea al momento actual.

##### Cómo se detecta el solapamiento

El chequeo vive en `JpaAppointmentRepository`, que es una **interfaz** sin implementación escrita a mano:

```java
boolean existsByProfessionalIdAndStartTimeBeforeAndEndTimeAfter(
        Long professionalId, LocalDateTime endTime, LocalDateTime startTime);
```

No tiene cuerpo porque es un **derived query method** de Spring Data JPA: al arrancar, Spring genera un proxy que implementa la interfaz y, leyendo el **nombre** del método, arma la consulta. El nombre se descompone así:

| Fragmento del nombre | Condición generada    |
|----------------------|-----------------------|
| `ProfessionalId`     | `professional.id = ?` |
| `StartTimeBefore`    | `startTime < ?`       |
| `EndTimeAfter`       | `endTime > ?`         |

Que se traduce (en JPQL, simplificado) a:

```sql
SELECT count(a) > 0 FROM Appointment a
WHERE a.professional.id = :professionalId
  AND a.startTime < :endTime
  AND a.endTime   > :startTime
```

Los parámetros se asignan **por posición**, en el orden en que aparecen en el nombre. Por eso en la llamada se pasan "cruzados": el `endTime` del turno nuevo se compara contra el `startTime` de los turnos existentes y viceversa. Dos intervalos se solapan cuando **uno empieza antes de que el otro termine y termina después de que el otro empieza**. Se usan comparaciones estrictas (`<`, `>`) a propósito: dos turnos contiguos (uno termina justo cuando el otro empieza, p. ej. `10–11` y `11–12`) **no** se consideran solapados.

La variante `...AndEndTimeAfterAndIdNot(..., id)` agrega `AND a.id <> :id` y se usa al actualizar, para que un turno no choque consigo mismo.

> El SQL real generado se puede ver en consola gracias a `spring.jpa.show-sql=true`.

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
| `403 Forbidden` | Autenticado pero sin permisos (p. ej. un no-ADMIN en `PATCH /{id}/role`). |
| `404 Not Found` | Recurso inexistente (usuario, profesional, service type o turno). |
| `409 Conflict` | `username`/`email` duplicado, o turno solapado. |
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

- `AppointmentCreatorServiceTest` / `AppointmentUpdaterServiceTest` — alta y actualización de turnos: rango horario inválido (`endTime <= startTime`), entidades inexistentes (user/professional/serviceType) y **detección de solapamiento** (incluyendo que un turno no choque consigo mismo en el `PUT`).
- `AppointmentFinderServiceTest` / `UserFinderServiceTest` — recuperación por id y `NotFoundException`.
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
├── entity/<entidad>/           # Entidad JPA
├── dto/
│   ├── request/<entidad>/      # Request DTO
│   └── response/<entidad>/     # Response DTO
├── repository/<entidad>/       # Repositorio JPA
├── exception/<entidad>/        # Excepciones específicas
├── service/<entidad>/          # Servicios (Finder, GetAll, Creator, Updater, Deleter)
└── controller/<entidad>/       # Controllers (uno por verbo HTTP)
```
