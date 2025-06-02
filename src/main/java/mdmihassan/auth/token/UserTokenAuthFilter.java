package mdmihassan.auth.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mdmihassan.util.Preconditions;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class UserTokenAuthFilter extends OncePerRequestFilter {

    private final TokenService<UserTokenAuthenticationToken> tokenService;

    public UserTokenAuthFilter(TokenService<UserTokenAuthenticationToken> tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("X-API-KEY");
        if (Preconditions.nonNullAndNonBlank(token)) {
            SecurityContextHelper.setAuthentication(tokenService.parse(token));
        }
        filterChain.doFilter(request, response);
    }

}