package mdmihassan.auth.token;

import mdmihassan.util.DateAndTime;
import mdmihassan.user.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserAuthenticationToken extends ExpirableAuthenticationToken {

    private final User user;

    public UserAuthenticationToken(User user) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities());
        this.user = user;
    }

    public UserAuthenticationToken(User user, Instant expiration) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities(), expiration);
        this.user = user;
    }

    public UserAuthenticationToken(User user, long timeout) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities(), timeout);
        this.user = user;
    }

    public UserAuthenticationToken(User user, Instant issuedAt, Instant expiration) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities(), issuedAt, expiration);
        this.user = user;
    }

    public static UserAuthenticationToken of(User user, Date issuedAt, Date expiration) {
        return new UserAuthenticationToken(user, DateAndTime.toInstant(issuedAt), DateAndTime.toInstant(expiration));
    }

    @Override
    public String getName() {
        return user.getUsername();
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserTokenAuthenticationToken test)) {
            return false;
        }
        if (!this.getAuthorities().equals(test.getAuthorities())) {
            return false;
        }
        if ((super.getDetails() == null) && (test.getDetails() != null)) {
            return false;
        }
        if ((super.getDetails() != null) && (test.getDetails() == null)) {
            return false;
        }
        if ((super.getDetails() != null) && (!super.getDetails().equals(test.getDetails()))) {
            return false;
        }
        if ((this.getCredentials() == null) && (test.getCredentials() != null)) {
            return false;
        }
        if ((this.getCredentials() != null) && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }
        if (this.getPrincipal() == null && test.getPrincipal() != null) {
            return false;
        }
        if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
            return false;
        }
        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
