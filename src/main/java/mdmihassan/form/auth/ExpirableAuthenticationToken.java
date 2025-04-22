package mdmihassan.form.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

public abstract class ExpirableAuthenticationToken extends AbstractAuthenticationToken {

    protected ExpirableAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public abstract Instant getIssuedAt();

    public abstract Instant getExpiration();

    public abstract  boolean isExpired();

}
