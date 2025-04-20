package mdmihassan.form.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mdmihassan.form.auth.SecurityContextHelper;
import mdmihassan.form.auth.TokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtTokenAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtTokenAuthFilter(@Qualifier("jwtTokenService") TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            SecurityContextHelper.setAuthentication(tokenService.verify(token.substring(7)));
        }
        filterChain.doFilter(request, response);
    }

}