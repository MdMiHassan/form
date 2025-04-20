package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

public class UserAuthorityToken {

    private final boolean authenticated;

    private final User user;

    private final Instant issuedAt;

    private final Instant expiration;

    public UserAuthorityToken(boolean authenticated,
                              User user,
                              Instant issuedAt,
                              Instant expiration) {
        this.authenticated = authenticated;
        this.user = user;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUserName() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public List<GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

}
