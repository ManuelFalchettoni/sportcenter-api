# sportcenter-api

Sistema de turnos para centro deportivo — Spring Boot REST API.

## Stack

- Java 21
- Spring Boot 4.0.6 (Web, Data JPA, Security, Validation)
- MySQL
- Maven

## Cómo correr

```bash
./mvnw spring-boot:run
```

La aplicación queda escuchando por defecto en `http://localhost:8080`.

---

## Entidades

### Professional

Profesional que presta servicios en el centro deportivo.

| Campo        | Tipo    | Restricciones          |
|--------------|---------|------------------------|
| `id`         | Long    | PK, autogenerado       |
| `name`       | String  | `@NotBlank`            |
| `speciality` | String  | `@NotBlank`            |
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

El campo `serviceTypeIds` es opcional. Cada id debe existir en `ServiceType`; caso contrario responde `404 Not Found`. En el `Response` los servicios se devuelven como `Set<ServiceTypeResponse>` bajo el campo `services`.

---

### ServiceType

Tipo de servicio que ofrece el centro (ej: sesión de kinesiología, clase de yoga, etc.).

| Campo             | Tipo       | Restricciones                |
|-------------------|------------|------------------------------|
| `id`              | Long       | PK, autogenerado             |
| `name`            | String     | `@NotBlank`                  |
| `durationMinutes` | int        | `@Positive`                  |
| `price`           | BigDecimal | `@NotNull`, `@PositiveOrZero` |

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
| `username`    | String        | `@NotBlank`, único                             |
| `email`       | String        | `@NotBlank`, `@Email`, único                   |
| `password`    | String        | `@NotBlank` — se guarda hasheado con BCrypt    |
| `role`        | UserEnum      | `ADMIN` o `USER` — fijado por el servidor, no aceptado en el body |
| `createdDate` | LocalDateTime | Generado por el servidor, no editable          |

#### Endpoints — base: `/sportcenter/users`

| Método | Path                  | Descripción                       | Respuesta                              |
|--------|-----------------------|-----------------------------------|----------------------------------------|
| GET    | `/{id}`               | Obtiene un usuario por id         | `200 OK` · `UserResponse`              |
| GET    | `?page=&size=&sort=`  | Lista paginada                    | `200 OK` · `Page<UserResponse>`        |
| POST   | `/`                   | Crea un usuario                   | `201 Created` · `UserResponse`         |
| PUT    | `/{id}`               | Actualiza un usuario              | `200 OK` · `UserResponse`              |
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

- `role` **no se acepta en el body**. Todo usuario creado vía este endpoint queda con rol `USER`. Para cambiar un rol existe (a futuro) un endpoint administrativo separado.
- En el `PUT`, si `password` viene vacío o nulo, no se actualiza.
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
| `user`         | User          | `@ManyToOne`, requerido — referenciado por `userId`          |
| `professional` | Professional  | `@ManyToOne`, requerido — referenciado por `professionalId`  |
| `serviceType`  | ServiceType   | `@ManyToOne`, requerido — referenciado por `serviceTypeId`   |

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
- En el `POST`, el turno se crea con `confirmed = false` y `createdAt` se setea al momento actual.

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
