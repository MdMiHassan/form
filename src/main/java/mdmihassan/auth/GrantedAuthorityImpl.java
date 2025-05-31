package mdmihassan.auth;

import lombok.Getter;
import mdmihassan.util.Preconditions;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class GrantedAuthorityImpl implements GrantedAuthority {

    private final String authority;

    public GrantedAuthorityImpl(String authority) {
        this.authority = Preconditions.requireNonNullAndNonBlankOrElseThrow(authority,
                "authority must not be null or empty");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GrantedAuthorityImpl grantedAuthority) {
            return authority.equals(grantedAuthority.getAuthority());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return authority.hashCode();
    }

    @Override
    public String toString() {
        return authority;
    }

}
