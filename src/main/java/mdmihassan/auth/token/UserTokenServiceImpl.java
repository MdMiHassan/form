package mdmihassan.auth.token;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mdmihassan.util.Preconditions;
import mdmihassan.util.DateAndTime;
import mdmihassan.user.AuthenticatedUserService;
import mdmihassan.user.User;
import mdmihassan.web.api.DuplicateResourceException;
import mdmihassan.web.api.ResourceNotFoundException;
import mdmihassan.web.api.UnauthorizedAccessException;
import mdmihassan.web.api.UnauthorizedActionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator stringKeyGenerator;

    @Override
    public UserTokenAuthenticationToken parse(String token) {
        return UserTokenAuthenticationToken.of(getUserTokenByEncodedHash(token));
    }

    @Override
    @Transactional
    public String generate(UserTokenAuthenticationToken userTokenAuthenticationToken) {
        return issueUserToken(userTokenAuthenticationToken).getSecret();
    }

    private UserTokenDto issueUserToken(UserTokenAuthenticationToken userTokenAuthenticationToken) {
        User user;
        if (userTokenAuthenticationToken.getPrincipal() instanceof User tokenUser) {
            user = tokenUser;
        } else {
            user = authenticatedUserService.loadAuthenticatedUser()
                    .orElseThrow(() -> new UnauthorizedActionException("User is required to create UserToken"));
        }

        if (userTokenRepository.existsByNameAndUser(userTokenAuthenticationToken.getName(), user)) {
            throw new DuplicateResourceException(
                    "token with then name '" + userTokenAuthenticationToken.getName() + "' already exists");
        }

        UserToken userToken = new UserToken();

        userToken.setName(userTokenAuthenticationToken.getName());
        userToken.setEnabled(userTokenAuthenticationToken.isEnabled());
        userToken.setUser(user);

        if (userTokenAuthenticationToken.getAuthorities() != null) {
            userToken.setTokenAuthorities(userTokenAuthenticationToken.getAuthorities());
        }

        userToken.setCredentialsExpiration(DateAndTime.toTimestamp(userTokenAuthenticationToken.getExpiration()));

        String secret = stringKeyGenerator.generateKey();
        userToken.setSecret(passwordEncoder.encode(secret));

        UserToken issuedToken = userTokenRepository.save(userToken);

        return UserTokenDto.builder()
                .id(issuedToken.getId())
                .name(issuedToken.getName())
                .secret(secret)
                .authorities(issuedToken.getAuthorities())
                .enabled(issuedToken.isEnabled())
                .issuedAt(DateAndTime.toInstant(issuedToken.getCreatedAt()))
                .expiration(DateAndTime.toInstant(issuedToken.getCredentialsExpiration()))
                .build();
    }

    @Override
    @Transactional
    public UserTokenDto issueUserToken(UserTokenDto userTokenDto) {
        UserTokenAuthenticationToken token = new UserTokenAuthenticationToken(new UserToken() {
            private List<GrantedAuthority> authorities;

            @Override
            public String getName() {
                return userTokenDto.getName();
            }

            @Override
            public User getUser() {
                return authenticatedUserService.loadAuthenticatedUser()
                        .orElseThrow(() -> new IllegalStateException("authenticated user required to issue new UserToken"));
            }

            @Override
            public List<GrantedAuthority> getAuthorities() {
                if (userTokenDto.getAuthorities() == null) {
                    return Collections.emptyList();
                }
                if (authorities != null) {
                    return authorities;
                }
                authorities = new ArrayList<>();
                authorities.addAll(userTokenDto.getAuthorities());
                return authorities;
            }

            @Override
            public boolean isEnabled() {
                return userTokenDto.getEnabled() == null || userTokenDto.getEnabled();
            }

            @Override
            public Timestamp getCredentialsExpiration() {
                return DateAndTime.toTimestamp(userTokenDto.getExpiration());
            }

        });

        return issueUserToken(token);
    }

    @Override
    public List<UserTokenDto> getAllIssuedTokens() {
        return authenticatedUserService.loadAuthenticatedUser()
                .map(userTokenRepository::findAllByUser)
                .map(userTokens -> {
                    if (userTokens.isEmpty()) {
                        throw new ResourceNotFoundException("No issued API key found");
                    }
                    return userTokens.stream()
                            .map(userToken -> UserTokenDto.builder()
                                    .id(userToken.getId())
                                    .name(userToken.getName())
                                    .authorities(userToken.getAuthorities())
                                    .enabled(userToken.isEnabled())
                                    .issuedAt(userToken.getCreatedAt().toInstant())
                                    .expiration(DateAndTime.toInstant(userToken.getCredentialsExpiration()))
                                    .build()
                            ).toList();
                })
                .orElseThrow(() ->
                        new UnauthorizedAccessException("Don't have enough permission to access issued tokens"));
    }

    @Override
    @Transactional
    public void setExpiration(UUID tokenId, Instant expiration) {
        UserToken userToken = getUserTokenIssuedByAuthenticatedUser(tokenId);
        userToken.setCredentialsExpiration(DateAndTime.toTimestamp(expiration));
        userTokenRepository.save(userToken);
    }

    private UserToken getUserTokenIssuedByAuthenticatedUser(UUID tokenId) {
        Optional<User> user = authenticatedUserService.loadAuthenticatedUser();
        if (user.isEmpty()) {
            throw new UnauthorizedActionException("Insufficient permissions to access the token");
        }
        return userTokenRepository.findByIdAndUser(tokenId, user.get())
                .orElseThrow(() -> new ResourceNotFoundException("Token not exists"));
    }


    private UserToken getUserTokenByEncodedHash(String hash) {
        return userTokenRepository.findBySecret(passwordEncoder.encode(hash))
                .orElseThrow(() -> new BadCredentialsException("API key not valid"));
    }

    @Override
    @Transactional
    public void invalidate(UUID tokenId) {
        UserToken userToken = getUserTokenIssuedByAuthenticatedUser(tokenId);
        userToken.disable();
        userTokenRepository.save(userToken);
    }

    @Override
    @Transactional
    public void invalidateAll() {
        authenticatedUserService.loadAuthenticatedUser()
                .ifPresent(user -> disableAllToken(userTokenRepository.findAllByUser(user)));
    }

    @Override
    @Transactional
    public void invalidateAllByToken(List<UUID> tokenIds) {
        authenticatedUserService.loadAuthenticatedUser()
                .ifPresent(user -> disableAllToken(userTokenRepository.findAllByIdInAndUser(tokenIds, user)));
    }

    private void disableAllToken(List<UserToken> userTokens) {
        userTokenRepository.saveAll(userTokens.stream()
                .filter(UserToken::isEnabled)
                .map(UserToken::disable)
                .toList());
    }

    @Override
    @Transactional
    public void delete(UUID tokenId) {
        userTokenRepository.delete(getUserTokenIssuedByAuthenticatedUser(tokenId));
    }

    @Override
    @Transactional
    public void deleteAll() {
        authenticatedUserService.loadAuthenticatedUser()
                .ifPresent(userTokenRepository::deleteAllByUser);
    }

    @Override
    @Transactional
    public void deleteAllByTokenId(List<UUID> tokenIds) {
        authenticatedUserService.loadAuthenticatedUser()
                .ifPresent(user -> userTokenRepository.deleteAllByIdInAndUser(tokenIds, user));
    }

    @Override
    @Transactional
    public void setAuthorities(UUID tokenId, List<? extends GrantedAuthority> authorities) {
        UserToken userToken = getUserTokenIssuedByAuthenticatedUser(tokenId);
        userToken.setTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

    @Override
    @Transactional
    public void addAuthorities(UUID tokenId, List<? extends GrantedAuthority> authorities) {
        UserToken userToken = getUserTokenIssuedByAuthenticatedUser(tokenId);
        userToken.addTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

    @Override
    @Transactional
    public UserTokenDto update(UUID tokenId, UserTokenUpdateRequest userTokenUpdateRequest) {
        UserToken userToken = getUserTokenIssuedByAuthenticatedUser(tokenId);
        if (Preconditions.nonNullAndNonBlank(userTokenUpdateRequest.getName())) {
            String spaceNormalizedName = StringUtils.normalizeSpace(userTokenUpdateRequest.getName());
            if (userTokenRepository.existsByNameAndUser(spaceNormalizedName, userToken.getUser())) {
                throw new DuplicateResourceException(
                        "token with then name '" + userTokenUpdateRequest.getName() + "' already exists");
            }
            userToken.setName(spaceNormalizedName);
        }

        if (userTokenUpdateRequest.getEnabled() != null) {
            userToken.setEnabled(userTokenUpdateRequest.getEnabled());
        }

        userToken.setCredentialsExpiration(DateAndTime.toTimestamp(userTokenUpdateRequest.getExpiration()));

        if (userTokenUpdateRequest.getAuthorities() != null) {
            userToken.setTokenAuthorities(userTokenUpdateRequest.getAuthorities());
        }

        UserToken updatedUserToken = userTokenRepository.save(userToken);

        return UserTokenDto.builder()
                .name(updatedUserToken.getName())
                .authorities(updatedUserToken.getAuthorities())
                .enabled(updatedUserToken.isEnabled())
                .issuedAt(updatedUserToken.getCreatedAt().toInstant())
                .expiration(DateAndTime.toInstant(userToken.getCredentialsExpiration()))
                .build();
    }

}
