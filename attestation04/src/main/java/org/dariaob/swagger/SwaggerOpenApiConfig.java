package org.dariaob.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации настроек отображения графического интерфейса SWAGGER
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Clinic application API",
                version = "1.0"
        ),
        security = {@SecurityRequirement(name = "jwtToken")}
)
@SecuritySchemes({
        @SecurityScheme(name = "jwtToken", type = SecuritySchemeType.HTTP, scheme = "bearer",
                bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
})
public class SwaggerOpenApiConfig {}