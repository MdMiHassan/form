package mdmihassan.form.service;

import mdmihassan.form.auth.UserTokenAuthentication;
import mdmihassan.form.dto.UserTokenDto;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserTokenService extends TokenService<UserTokenAuthentication> {

    UserTokenDto issueUserToken(UserTokenAuthentication userTokenAuthentication);

    List<UserTokenDto> getAllIssuedTokens();

    void setExpiration(UUID tokenId, Instant expiration);

    void invalidate(UUID tokenId);

    void invalidateAll();

    void invalidateAllByToken(List<UUID> tokens);

    void delete(UUID tokenId);

    void deleteAll();

    void deleteAllByTokenId(List<UUID> tokenIds);

    void setAuthorities(UUID tokenId, List<GrantedAuthority> authorities);

    void addAuthorities(UUID tokenId, List<GrantedAuthority> authorities);

}
