package pl.matkmiec.backup.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configuration for CORS and other web-related settings.
 * Allows requests from the frontend to the backend.
 * */
public class WebConfig implements WebMvcConfigurer {

    /** Configure Cross-Origin Resource Sharing (CORS) for the backend. */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
