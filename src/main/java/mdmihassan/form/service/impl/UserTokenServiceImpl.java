package mdmihassan.form.service.impl;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.UserTokenAuthentication;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.entity.UserToken;
import mdmihassan.form.exception.ResourceNotFoundException;
import mdmihassan.form.exception.UnauthorizedActionException;
import mdmihassan.form.repository.UserTokenRepository;
import mdmihassan.form.service.UserService;
import mdmihassan.form.service.UserTokenService;
import mdmihassan.form.util.TimeAndDates;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator stringKeyGenerator;

    @Override
    public UserTokenAuthentication parse(String token) {
        return UserTokenAuthentication.of(getUserTokenByEncodedHash(token));
    }

    @Override
    public String generate(UserTokenAuthentication apiAccessToken) {
        return createUserToken(apiAccessToken);
    }

    private String createUserToken(UserTokenAuthentication userTokenAuthentication) {
        UserToken userToken = new UserToken();

        if (userTokenAuthentication instanceof UserTokenAuthentication token) {
            userToken.setName(token.getName());
        }

        if (userTokenAuthentication.getUser() != null) {
            userToken.setUser(userTokenAuthentication.getUser());
        } else {
            userToken.setUser(userService.getAuthenticatedUser().orElse(null));
        }

        userToken.setTokenAuthorities(userTokenAuthentication.getAuthorities());
        if (userTokenAuthentication.getExpiration() != null) {
            userToken.setCredentialsExpiration(Timestamp.from(userTokenAuthentication.getExpiration()));
        }

        userToken.setEnabled(true);

        String generatedKey = stringKeyGenerator.generateKey();
        userToken.setTokenHash(passwordEncoder.encode(generatedKey));

        userTokenRepository.save(userToken);
        return generatedKey;
    }

    @Override
    public UserTokenDto issueUserToken(UserTokenAuthentication userTokenAuthentication) {
        String apiSecret = createUserToken(userTokenAuthentication);
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setName(userTokenAuthentication.getName());
        userTokenDto.setSecret(apiSecret);
        userTokenDto.setEnabled(true);
        userTokenDto.setIssuedAt(userTokenAuthentication.getIssuedAt());
        userTokenDto.setExpiration(userTokenAuthentication.getExpiration());
        userTokenDto.setAuthorities(userTokenAuthentication.getAuthorities());
        return userTokenDto;
    }

    @Override
    public List<UserTokenDto> getAllIssuedTokens() {
        return userService.getAuthenticatedUser()
                .map(userTokenRepository::findAllByUser)
                .map(userTokens -> userTokens.stream()
                        .map(userToken -> UserTokenDto.builder()
                                .name(userToken.getName())
                                .issuedAt(userToken.getCreatedAt().toInstant())
                                .enabled(userToken.isEnabled())
                                .expiration(TimeAndDates.toInstant(userToken.getCredentialsExpiration()))
                                .authorities(userToken.getTokenAuthorities())
                                .build()
                        ).toList())
                .orElseThrow(() -> new ResourceNotFoundException("don't found any issued token"));
    }

    @Override
    public void setExpiration(UUID tokenId, Instant expiration) {
        UserToken userToken = getAuthorizedUserToken(tokenId)
                .orElseThrow(() ->
                        new UnauthorizedActionException("Insufficient permissions to change expiration of this token"));
        userToken.setCredentialsExpiration(Timestamp.from(expiration));
        userTokenRepository.save(userToken);
    }

    private Optional<UserToken> getAuthorizedUserToken(UUID tokenId) {
        return userService.getAuthenticatedUser()
                .map(user -> userTokenRepository.findByIdAndUser(tokenId, user));
    }


    private UserToken getUserTokenByEncodedHash(String hash) {
        return userTokenRepository.findByTokenHash(passwordEncoder.encode(hash))
                .orElseThrow(() -> new BadCredentialsException("API key not valid"));
    }

    @Override
    public void invalidate(UUID tokenId) {
        UserToken userToken = getAuthorizedUserToken(tokenId)
                .orElseThrow(() ->
                        new UnauthorizedActionException("Insufficient permissions to invalidate this token"));
        userToken.disable();
        userTokenRepository.save(userToken);
    }

    @Override
    public void invalidateAll() {
        userService.getAuthenticatedUser()
                .ifPresent(user -> disableAllToken(userTokenRepository.findAllByUser(user)));
    }

    @Override
    public void invalidateAllByToken(List<UUID> tokenIds) {
        userService.getAuthenticatedUser()
                .ifPresent(user -> disableAllToken(userTokenRepository.findAllByIdInAndUser(tokenIds, user)));
    }

    private void disableAllToken(List<UserToken> userTokens) {
        userTokenRepository.saveAll(userTokens.stream()
                .filter(UserToken::isEnabled)
                .peek(UserToken::disable)
                .toList());
    }

    @Override
    public void delete(UUID tokenId) {
        userTokenRepository.delete(getAuthorizedUserToken(tokenId)
                .orElseThrow(() -> new UnauthorizedActionException("Insufficient permissions to delete this token")));
    }

    @Override
    public void deleteAll() {
        userService.getAuthenticatedUser()
                .ifPresent(userTokenRepository::deleteAllByUser);
    }

    @Override
    public void deleteAllByTokenId(List<UUID> tokenIds) {
        userService.getAuthenticatedUser()
                .ifPresent(user -> userTokenRepository.deleteAllByIdInAndUser(tokenIds, user));
    }

    @Override
    public void setAuthorities(UUID tokenId, List<GrantedAuthority> authorities) {
        UserToken userToken = getAuthorizedUserToken(tokenId)
                .orElseThrow(() -> new UnauthorizedActionException("\"Insufficient permissions to set authorities"));
        userToken.setTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

    @Override
    public void addAuthorities(UUID tokenId, List<GrantedAuthority> authorities) {
        UserToken userToken = getAuthorizedUserToken(tokenId)
                .orElseThrow(() -> new UnauthorizedActionException("Insufficient permissions to add authorities"));
        userToken.addTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

}
