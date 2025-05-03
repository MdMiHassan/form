package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserAuthenticationProvider extends DaoAuthenticationProvider {

    public UserAuthenticationProvider() {
        super();
    }

    public UserAuthenticationProvider(PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        if (userDetails instanceof User user) {
            if (!user.isPrimaryEmailVerified()) {
                throw new PrimaryEmailNotVerifiedException("email address not verified");
            }
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }

}
