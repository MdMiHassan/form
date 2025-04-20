package mdmihassan.form.auth;

import mdmihassan.form.model.ApiKey;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserTokenService extends TokenService {

    ApiKey issueKey(ApiAccessToken apiAccessToken);

    List<ApiKey> getAllIssuedKeys();

    void setExpiration(UUID token, Instant expiration);

    void invalidate(UUID token);

    void invalidateAll();

    void invalidateAllByToken(List<UUID> tokens);

    void delete(UUID token);

    void deleteAll();

    void deleteAllByToken(List<UUID> tokens);

    void setAuthorities(UUID token, List<GrantedAuthority> authorities);

    void addAuthorities(UUID token, List<GrantedAuthority> authorities);
}
