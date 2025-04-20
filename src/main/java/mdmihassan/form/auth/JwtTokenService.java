package mdmihassan.form.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mdmihassan.form.entity.User;
import mdmihassan.form.service.UserService;
import mdmihassan.form.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("jwtTokenService")
public class JwtTokenService implements TokenService {

    private final SecretKey secretKey;
    private final UserService userService;

    @Override
    public UserAuthorityToken verify(String token) {
        try {
            Claims claims = JwtUtil.parseClaims(token, secretKey).getPayload();
            Optional<User> user = userService.get(UUID.fromString(claims.getSubject()));
            if (JwtUtil.nonExpired(claims) || user.isEmpty()) {
                throw new BadCredentialsException("invalid token");
            }
            User targetUser = user.get();
            if (targetUser.isLocked()) {
                throw new BadCredentialsException("user locked");
            }
            if (!targetUser.isEnabled()) {
                throw new BadCredentialsException("user disabled");
            }

            return new UserAuthorityToken(true,
                    targetUser,
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant());

        } catch (IllegalArgumentException | JwtException e) {
            throw new BadCredentialsException("token not valid", e);
        }
    }

    @Override
    public String generate(UserAuthorityToken userAuthorityToken) {
        Map<String, Object> claims = Map.of("roles", userAuthorityToken.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return JwtUtil.generateJwt(secretKey,
                userAuthorityToken.getUserName(),
                userAuthorityToken.getIssuedAt(),
                userAuthorityToken.getExpiration(),
                claims);
    }

}