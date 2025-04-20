package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

public class GrantedAuthorityToken extends UserAuthorityToken {

    private final List<GrantedAuthority> authorities;

    public GrantedAuthorityToken(boolean authenticated,
                                 User user,
                                 Instant issuedAt,
                                 Instant expiration,
                                 List<GrantedAuthority> authorities) {
        super(authenticated, user, issuedAt, expiration);
        this.authorities = authorities;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
