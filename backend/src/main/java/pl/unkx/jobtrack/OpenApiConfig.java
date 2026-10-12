package pl.unkx.jobtrack;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Lets Swagger UI send the JWT from /api/auth/login via the "Authorize" button. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "JobTrack API", version = "1.0",
                description = "Job application tracker. Log in via /api/auth/login, then click Authorize and paste the token."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
class OpenApiConfig {
}
