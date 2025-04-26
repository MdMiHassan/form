package mdmihassan.form.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

public abstract class ExpirableAuthenticationToken extends AbstractAuthenticationToken {

    private final Instant issuedAt;
    private Instant expiration;

    protected ExpirableAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.issuedAt = Instant.now();
    }

    protected ExpirableAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Instant expiration) {
        super(authorities);
        this.issuedAt = Instant.now();
        this.expiration = expiration;
    }

    protected ExpirableAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Instant issuedAt, Instant expiration) {
        super(authorities);
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return !nonExpired();
    }

    protected boolean nonExpired() {
        if (expiration == null) {
            return true;
        }
        return expiration.isBefore(Instant.now());
    }

}
