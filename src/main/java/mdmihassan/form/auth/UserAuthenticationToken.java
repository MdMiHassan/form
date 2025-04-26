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

    public UserAuthenticationToken(User user) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities());
        this.user = user;
    }

    public UserAuthenticationToken(User user, Instant expiration) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities(), expiration);
        this.user = user;
    }

    public UserAuthenticationToken(User user, Instant issuedAt, Instant expiration) {
        super(Objects.requireNonNull(user, "user must not be null").getAuthorities(), issuedAt, expiration);
        this.user = user;
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
    public List<GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getUser();
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
        if (!(obj instanceof UserTokenAuthentication test)) {
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
