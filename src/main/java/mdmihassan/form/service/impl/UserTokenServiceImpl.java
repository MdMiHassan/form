package mdmihassan.form.service.impl;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.UserTokenAuthentication;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.entity.User;
import mdmihassan.form.entity.UserToken;
import mdmihassan.form.exception.ResourceNotFoundException;
import mdmihassan.form.exception.UnauthorizedActionException;
import mdmihassan.form.repository.UserTokenRepository;
import mdmihassan.form.service.UserService;
import mdmihassan.form.service.UserTokenService;
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
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator stringKeyGenerator;

    @Override
    public UserTokenAuthentication parse(String token) {
        return UserTokenAuthentication.of(getUserToken(token));
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
            userToken.setUser(userService.getCurrentUser());
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
        List<UserToken> userTokens = userTokenRepository.findAllByUser(userService.getCurrentUser());
        if (userTokens.isEmpty()) {
            throw new ResourceNotFoundException("No issued api key found");
        }
        return userTokens.stream()
                .map(userToken -> UserTokenDto.builder()
                        .name(userToken.getName())
                        .issuedAt(userToken.getCreatedAt().toInstant())
                        .enabled(userToken.isEnabled())
                        .expiration(userToken.getCredentialsExpiration() == null ? null : userToken.getCredentialsExpiration().toInstant())
                        .authorities(userToken.getTokenAuthorities())
                        .build()
                ).toList();
    }

    @Override
    public void setExpiration(UUID tokenId, Instant expiration) {
        UserToken userToken = userTokenRepository.findById(tokenId)
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
    public void invalidate(UUID tokenId) {
        UserToken userToken = userTokenRepository.findById(tokenId)
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
        disableAllToken(userTokenRepository.findAllByUser(userService.getCurrentUser()));
    }

    @Override
    public void invalidateAllByToken(List<UUID> tokenIds) {
        disableAllToken(userTokenRepository.findAllByUserIdAndIdIn(userService.getCurrentUser(), tokenIds));
    }

    private void disableAllToken(List<UserToken> userTokens) {
        userTokenRepository.saveAll(userTokens.stream()
                .filter(UserToken::isEnabled)
                .peek(UserToken::disable)
                .toList());
    }

    @Override
    public void delete(UUID tokenId) {
        UserToken userToken = userTokenRepository.findById(tokenId)
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
    public void deleteAllByTokenId(List<UUID> tokenIds) {
        userTokenRepository.deleteAllByUserAndIdIn(userService.getCurrentUser(), tokenIds);
    }

    @Override
    public void setAuthorities(UUID tokenId, List<GrantedAuthority> authorities) {
        UserToken userToken = userTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        if (!userService.getCurrentUser().getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to set authorities");
        }
        userToken.setTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

    @Override
    public void addAuthorities(UUID tokenId, List<GrantedAuthority> authorities) {
        UserToken userToken = userTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("api key not exists"));
        if (!userService.getCurrentUser().getId().equals(userToken.getUser().getId())) {
            throw new UnauthorizedActionException("You don't have necessary permission to add new authorities");
        }
        userToken.addTokenAuthorities(authorities);
        userTokenRepository.save(userToken);
    }

}
