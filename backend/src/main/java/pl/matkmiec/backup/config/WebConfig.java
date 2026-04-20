package pl.matkmiec.backup.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuration for CORS and other web-related settings.
 * Allows requests from the frontend to the backend.
 * */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /** Configure Cross-Origin Resource Sharing (CORS) for the backend. */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        log.info("Configuring CORS for origin");
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
        log.debug("CORS configured successfully");
    }
}
