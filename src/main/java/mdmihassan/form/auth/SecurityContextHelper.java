package mdmihassan.form.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void setAuthentication(UserAuthorityToken userAuthorityToken) {
        if (userAuthorityToken != null && userAuthorityToken.isAuthenticated()) {
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(userAuthorityToken.getUser(),
                            null, userAuthorityToken.getAuthorities()));
        }
    }

}
