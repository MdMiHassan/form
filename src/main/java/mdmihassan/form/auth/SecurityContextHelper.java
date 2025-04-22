package mdmihassan.form.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void setAuthentication(UserAuthenticationToken userAuthenticationToken) {
        SecurityContextHolder.getContext().setAuthentication(userAuthenticationToken);
    }

}
