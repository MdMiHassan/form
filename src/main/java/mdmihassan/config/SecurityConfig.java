package mdmihassan.config;

import io.jsonwebtoken.security.Keys;
import mdmihassan.auth.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${form.security.config.secret.key}")
    private String secretKey;

    @Bean
    public Base64StringKeyGenerator base64StringKeyGenerator() {
        return new Base64StringKeyGenerator();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserAuthenticationProvider userAuthenticationProvider(UserDetailsService userDetailsService,
                                                                 PasswordEncoder passwordEncoder) {
        UserAuthenticationProvider userAuthenticationProvider = new UserAuthenticationProvider(passwordEncoder);
        userAuthenticationProvider.setUserDetailsService(userDetailsService);
        return userAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
