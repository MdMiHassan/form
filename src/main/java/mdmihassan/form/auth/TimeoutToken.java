package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

public class TimeoutToken extends GrantedAuthorityToken {

    private TimeoutToken(boolean authenticated, User user, Instant issuedAt, long timeout, List<GrantedAuthority> authorities) {
        super(authenticated, user, issuedAt, issuedAt.plusMillis(timeout), authorities);
    }

    public static TimeoutToken fromNow(boolean authenticated, User user, long timeout, List<GrantedAuthority> authorities) {
        return new TimeoutToken(authenticated, user, Instant.now(), timeout, authorities);
    }

}
