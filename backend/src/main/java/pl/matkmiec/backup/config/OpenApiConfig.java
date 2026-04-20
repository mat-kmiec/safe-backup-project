package pl.matkmiec.backup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for OpenAPI documentation. */
@Configuration
@Slf4j
public class OpenApiConfig {

    /* Custom OpenAPI configuration
    * @return OpenAPI object with custom security scheme and info */
    @Bean
    public OpenAPI customOpenApi() {
        log.info("Initializing OpenAPI configuration");
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("Safe Backup API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));

    }
}
