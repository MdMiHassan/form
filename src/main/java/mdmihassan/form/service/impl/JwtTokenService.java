package mdmihassan.form.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mdmihassan.form.auth.UserAuthenticationToken;
import mdmihassan.form.entity.User;
import mdmihassan.form.service.TokenService;
import mdmihassan.form.service.UserService;
import mdmihassan.form.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService implements TokenService<UserAuthenticationToken> {

    private final SecretKey secretKey;
    private final UserService userService;

    @Override
    public UserAuthenticationToken parse(String token) {
        try {
            Claims claims = JwtUtil.parseClaims(token, secretKey).getPayload();
            return UserAuthenticationToken.of((User) userService.loadUserByUsername(claims.getSubject()),
                    claims.getIssuedAt(),
                    claims.getExpiration());
        } catch (IllegalArgumentException | JwtException e) {
            throw new BadCredentialsException("token not valid", e);
        }
    }

    @Override
    public String generate(UserAuthenticationToken userAuthenticationToken) {
        Map<String, Object> claims = Map.of("roles", userAuthenticationToken.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return JwtUtil.generateJwt(secretKey,
                userAuthenticationToken.getName(),
                userAuthenticationToken.getIssuedAt(),
                userAuthenticationToken.getExpiration(),
                claims);
    }

}