package mdmihassan.form.auth;

import mdmihassan.form.entity.User;
import mdmihassan.form.util.TimeAndDates;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserAuthenticationToken extends ExpirableAuthenticationToken {

    private final User user;
    private Instant issuedAt;
    private Instant expiration;

    public UserAuthenticationToken(User user) {
        super(user.getAuthorities());
        this.user = Objects.requireNonNull(user, "user must not be null");
    }

    public UserAuthenticationToken(User user, Instant issuedAt, Instant expiration) {
        super(user.getAuthorities());
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public static UserAuthenticationToken of(User user, Date issuedAt, Date expiration) {
        return new UserAuthenticationToken(user, TimeAndDates.toInstant(issuedAt), TimeAndDates.toInstant(expiration));
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Instant getIssuedAt() {
        return issuedAt;
    }

    @Override
    public Instant getExpiration() {
        return expiration;
    }

    @Override
    public boolean isExpired() {
        return !nonExpired();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return nonExpired()
                && user.isEnabled()
                && user.isCredentialsNonExpired()
                && user.isAccountNonLocked()
                && user.isAccountNonExpired();
    }

    private boolean nonExpired() {
        if (expiration == null) {
            return true;
        }
        return expiration.isBefore(Instant.now());
    }

}
