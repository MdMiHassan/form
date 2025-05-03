package mdmihassan.form.auth;

import mdmihassan.form.entity.UserToken;
import mdmihassan.form.util.TimeAndDates;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserTokenAuthenticationToken extends UserAuthenticationToken {

    private final UserToken userToken;

    public UserTokenAuthenticationToken(UserToken userToken) {
        super(userToken.getUser(),
                TimeAndDates.toInstant(userToken.getCreatedAt()),
                TimeAndDates.toInstant(userToken.getCredentialsExpiration()));
        this.userToken = userToken;
    }

    public static UserTokenAuthenticationToken of(UserToken userToken) {
        return new UserTokenAuthenticationToken(userToken);
    }

    @Override
    public Object getPrincipal() {
        return userToken;
    }

    @Override
    public Object getCredentials() {
        return userToken.getSecret();
    }

    @Override
    public String getName() {
        return userToken.getName();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return userToken.getAuthorities();
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated() && userToken.isTokenAuthenticated();
    }

    public boolean isEnabled() {
        return userToken.isEnabled();
    }

}
