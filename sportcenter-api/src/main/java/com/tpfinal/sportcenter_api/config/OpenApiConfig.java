package com.tpfinal.sportcenter_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI (springdoc).
 *
 * springdoc deduce rutas, parámetros y esquemas leyendo los controllers y DTOs,
 * pero no puede deducir CÓMO se autentica la API. Acá se declara el esquema
 * Bearer JWT: con esto Swagger UI muestra el botón "Authorize", y la spec les
 * dice a los generadores de clientes (openapi-typescript, orval) que cada
 * request lleva el header Authorization.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI sportcenterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sportcenter API")
                        .version("v1")
                        .description("""
                                Sistema de turnos para centro deportivo.
                                Login en POST /sportcenter/auth/login para obtener el token; \
                                usar el botón Authorize con ese token para probar los endpoints protegidos. \
                                Las reglas de negocio (solapamientos, ownership, ciclo de vida del turno) \
                                están documentadas en el README del repositorio."""))
                // Requirement global: marca todos los endpoints como protegidos por
                // defecto en la doc (los públicos, login/registro, funcionan igual).
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
