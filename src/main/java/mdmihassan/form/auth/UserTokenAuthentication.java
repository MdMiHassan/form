package mdmihassan.form.auth;

import mdmihassan.form.entity.UserToken;
import mdmihassan.form.util.TimeAndDates;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserTokenAuthentication extends UserAuthenticationToken {

    private final UserToken userToken;

    public UserTokenAuthentication(UserToken userToken) {
        super(userToken.getUser(),
                TimeAndDates.toInstant(userToken.getCreatedAt()),
                TimeAndDates.toInstant(userToken.getCredentialsExpiration()));
        this.userToken = userToken;
    }

    public static UserTokenAuthentication of(UserToken userToken) {
        return new UserTokenAuthentication(userToken);
    }

    @Override
    public String getName() {
        return userToken.getName();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return userToken.getTokenAuthorities();
    }

}
