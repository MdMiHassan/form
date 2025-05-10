package mdmihassan.form.service;

import mdmihassan.form.auth.UserTokenAuthenticationToken;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.model.UserTokenUpdateRequest;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserTokenService extends TokenService<UserTokenAuthenticationToken> {

    UserTokenDto issueUserToken(UserTokenDto userTokenDto);

    List<UserTokenDto> getAllIssuedTokens();

    void setExpiration(UUID tokenId, Instant expiration);

    void invalidate(UUID tokenId);

    void invalidateAll();

    void invalidateAllByToken(List<UUID> tokens);

    void delete(UUID tokenId);

    void deleteAll();

    void deleteAllByTokenId(List<UUID> tokenIds);

    void setAuthorities(UUID tokenId, List<? extends GrantedAuthority> authorities);

    void addAuthorities(UUID tokenId, List<? extends GrantedAuthority> authorities);

    UserTokenDto update(UUID tokenId, UserTokenUpdateRequest userTokenUpdateRequest);

}
