package mdmihassan.form.auth;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.entity.User;
import mdmihassan.form.entity.UserToken;
import mdmihassan.form.exception.ResourceNotFoundException;
import mdmihassan.form.exception.UnauthorizedActionException;
import mdmihassan.form.model.ApiKey;
import mdmihassan.form.repository.UserTokenRepository;
import mdmihassan.form.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Qualifier("apiTokenService")
public class ApiAccessTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator stringKeyGenerator;

    @Override
    public UserAuthorityToken verify(String token) {
        UserToken userToken = getUserToken(token);
        if (userToken.getCredentialsExpiration() != null
                && userToken.getCredentialsExpiration().after(Timestamp.from(Instant.now()))) {
            throw new BadCredentialsException("token expired");
        }
        User user = userToken.getUser();
        if (!user.isEnabled()) {
            throw new BadCredentialsException("user not available");
        }
        if (user.isLocked()) {
            throw new BadCredentialsException("user locked");
        }
        if (user.getAccountExpiration() != null
                && user.getCredentialsExpiration().after(Timestamp.from(Instant.now()))) {
            throw new BadCredentialsException("user account expired");
        }
        return new ApiAccessToken(
                true,
                user,
                userToken.getCreatedAt().toInstant(),
                userToken.getCredentialsExpiration() == null ? null : userToken.getCredentialsExpiration().toInstant(),
                userToken.getTokenAuthorities(),
                userToken.getName());
    }

    @Override
    public String generate(UserAuthorityToken userAuthorityToken) {
        UserToken userToken = new UserToken();

        if (userAuthorityToken instanceof ApiAccessToken token) {
            userToken.setName(token.getName());
        }

        if (userAuthorityToken.getUser() != null) {
            userToken.setUser(userAuthorityToken.getUser());
        } else {
            userToken.setUser(userService.getCurrentUser());
        }

        userToken.setTokenAuthorities(userAuthorityToken.getAuthorities());
        if (userAuthorityToken.getExpiration() != null) {
            userToken.setCredentialsExpiration(Timestamp.from(userAuthorityToken.getExpiration()));
        }

        userToken.setEnabled(true);

        String generatedKey = stringKeyGenerator.generateKey();
        userToken.setTokenHash(passwordEncoder.encode(generatedKey));

        userTokenRepository.save(userToken);
        return generatedKey;
    }

    @Override
    public ApiKey issueKey(ApiAccessToken apiAccessToken) {
        String apiSecret = generate(apiAccessToken);
        ApiKey apiKey = new ApiKey();
        apiKey.setName(apiAccessToken.getName());
        apiKey.setSecret(apiSecret);
        apiKey.setEnabled(true);
        apiKey.setIssuedAt(apiAccessToken.getIssuedAt());
        apiKey.setExpiration(apiAccessToken.getExpiration());
        apiKey.setAuthorities(apiAccessToken.getAuthorities());
        return apiKey;
    }

    @Override
    public List<ApiKey> getAllIssuedKeys() {
        List<UserToken> userTokens = userTokenRepository.findAllByUser(userService.getCurrentUser());
        if (userTokens.isEmpty()) {
            throw new ResourceNotFoundException("No issued api key found");
        }
        return userTokens.stream()
                .map(userToken -> ApiKey.builder()
                        .name(userToken.getName())
                        .issuedAt(userToken.getCreatedAt().toInstant())
                        .enabled(userToken.isEnabled())
                        .expiration(userToken.getCredentialsExpiration() == null ? null : userToken.getCredentialsExpiration().toInstant())
                        .authorities(userToken.getTokenAuthorities())
                        .build()
                ).toList();
    }

    @Override
    public void setExpiration(UUID id, Instant expiration) {
        UserToken userToken = userTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to change expiration of this token");
        }
        userToken.setCredentialsExpiration(Timestamp.from(expiration));
        userTokenRepository.save(userToken);
    }

    private UserToken getUserToken(String token) {
        return userTokenRepository.findByTokenHash(passwordEncoder.encode(token))
                .orElseThrow(() -> new BadCredentialsException("invalid token"));
    }

    @Override
    public void invalidate(UUID id) {
        UserToken userToken = userTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to invalidate this token");
        }
        userToken.disable();
        userTokenRepository.save(userToken);
    }

    @Override
    public void invalidateAll() {
        List<UserToken> userTokens = userTokenRepository.findAllByUser(userService.getCurrentUser())
                .stream()
                .filter(UserToken::isValidToken)
                .peek(UserToken::disable)
                .toList();
        userTokenRepository.saveAll(userTokens);
    }

    @Override
    public void invalidateAllByToken(List<UUID> tokenIds) {
        List<UserToken> userTokens = userTokenRepository.findAllByUserIdAndIdIn(userService.getCurrentUser(), tokenIds)
                .stream()
                .filter(UserToken::isValidToken)
                .peek(UserToken::disable)
                .toList();
        userTokenRepository.saveAll(userTokens);
    }

    @Override
    public void delete(UUID id) {
        UserToken userToken = userTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        if (!userService.getCurrentUser().getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to delete this token");
        }
        userTokenRepository.delete(userToken);
    }

    @Override
    public void deleteAll() {
        userTokenRepository.deleteAllByUser(userService.getCurrentUser());
    }

    @Override
    public void deleteAllByToken(List<UUID> tokenIds) {
        userTokenRepository.deleteAllByUserAndIdIn(userService.getCurrentUser(), tokenIds);
    }

    @Override
    public void setAuthorities(UUID id, List<GrantedAuthority> authorities) {
        UserToken userToken = userTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        if (!userService.getCurrentUser().getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to set authorities");
        }
        userToken.setTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

    @Override
    public void addAuthorities(UUID id, List<GrantedAuthority> authorities) {
        UserToken userToken = userTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        if (!userService.getCurrentUser().getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to add new authorities");
        }
        userToken.addTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

}
