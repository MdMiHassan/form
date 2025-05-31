package mdmihassan.auth.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

    public static void setAuthentication(Authentication authentication) {
        Authentication presentedAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (presentedAuthentication == null || !presentedAuthentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

}
