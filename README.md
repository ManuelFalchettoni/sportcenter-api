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
  "active": true
}
```

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
