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

La aplicación queda escuchando en `http://localhost:8080`.

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

#### Endpoints — base: `/sportcenter/professional`

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

#### Endpoints — base: `/sportcenter/service-type`

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
| `role`        | UserEnum      | `@NotNull` — `ADMIN` o `USER`                  |
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
  "password": "secret123",
  "role": "ADMIN"
}
```

En el `PUT`, si `password` viene vacío o nulo, no se actualiza. `createdDate` se setea automáticamente en el `POST` y no puede modificarse.

---

## Paginación

Los endpoints `GET` (listado) aceptan los query params estándar de Spring Data:

- `page` — número de página (0-indexed). Default `0`.
- `size` — cantidad de elementos por página. Default `20`.
- `sort` — campo y dirección. Ej: `sort=name,asc` o `sort=price,desc`.

Ejemplo:

```
GET /sportcenter/service-type?page=0&size=10&sort=price,desc
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
