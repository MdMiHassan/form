package mdmihassan.config;

import lombok.Data;
import mdmihassan.util.Preconditions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(DefaultCorsProperties defaultCorsProperties) {
        Map<String, CorsConfiguration> configs = new HashMap<>();

        if (defaultCorsProperties.getOrigins() != null
                && Preconditions.nonNullAndNonEmpty(defaultCorsProperties.getOrigins().getAllowed())) {
            for (DefaultCorsProperties.Origins.Origin allowedOrigin : defaultCorsProperties.getOrigins().getAllowed()) {
                if (allowedOrigin.getUrl() != null
                        && Preconditions.nonNullAndNonEmpty(allowedOrigin.getMethods())) {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of(allowedOrigin.getUrl().toString()));
                    cfg.setAllowedMethods(allowedOrigin.getMethods()
                            .stream()
                            .map(RequestMethod::name)
                            .toList());
                    cfg.applyPermitDefaultValues();
                    configs.put(allowedOrigin.getUrl().toString(), cfg);
                }
            }
        }

        return request -> configs.get(request.getHeader("Origin"));
    }

    @Component
    @ConfigurationProperties(prefix = "form.security.cors")
    @Data
    public static class DefaultCorsProperties {

        private Origins origins;

        @Data
        public static class Origins {
            private List<Origin> allowed;

            @Data
            public static class Origin {
                private URL url;
                private List<RequestMethod> methods;
            }

        }

    }

}
