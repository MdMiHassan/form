package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

public class ApiAccessToken extends GrantedAuthorityToken {

    private final List<GrantedAuthority> authorities;
    private final String name;

    public ApiAccessToken(boolean authenticated,
                          User user,
                          Instant issuedAt,
                          Instant expiration,
                          List<GrantedAuthority> authorities,
                          String name) {
        super(authenticated, user, issuedAt, expiration, authorities);
        this.authorities = authorities;
        this.name = name;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getName() {
        return name;
    }

}
