package mdmihassan.form.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.net.URL;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${form.security.api.cors.allowed.origins}")
    private List<URL> defaultAllowedOrigins;

    @Value("${form.security.api.cors.allowed.methods}")
    private List<RequestMethod> defaultAllowedMethods;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(defaultAllowedOrigins.stream().map(URL::toString).toList());
            corsConfiguration.setAllowedMethods(defaultAllowedMethods.stream().map(RequestMethod::name).toList());
            return corsConfiguration.applyPermitDefaultValues();
        };
    }

}
