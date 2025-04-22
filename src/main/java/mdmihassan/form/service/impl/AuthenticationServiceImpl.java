package mdmihassan.form.service.impl;

import lombok.extern.slf4j.Slf4j;
import mdmihassan.form.auth.UserAuthenticationToken;
import mdmihassan.form.entity.User;
import mdmihassan.form.model.AccessTokenRequest;
import mdmihassan.form.model.AuthenticationResponse;
import mdmihassan.form.model.LoginRequest;
import mdmihassan.form.service.AuthenticationService;
import mdmihassan.form.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService<UserAuthenticationToken> tokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${form.security.api.key.refresh.timeout}")
    private long refreshKeyTimeout;

    @Value("${form.security.api.key.access.timeout}")
    private long accessKeyTimeout;

    public AuthenticationServiceImpl(TokenService<UserAuthenticationToken> tokenService,
                                     AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(unauthenticated);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Incorrect Username or Password");
        }
        User user = (User) authentication.getPrincipal();
        return AuthenticationResponse.of(issueRefreshToken(user), issueAccessToken(user));
    }

    @Override
    public AuthenticationResponse getAccessToken(AccessTokenRequest accessTokenRequest) {
        UserAuthenticationToken userAuthenticationToken = tokenService.parse(accessTokenRequest.getRefreshToken());
        if (!userAuthenticationToken.isAuthenticated()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return AuthenticationResponse.of(accessTokenRequest.getRefreshToken(),
                issueAccessToken(userAuthenticationToken.getUser()));
    }

    private String issueAccessToken(User user) {
        Instant issuedAt = Instant.now();
        return tokenService.generate(new UserAuthenticationToken(user, issuedAt, issuedAt.plusMillis(accessKeyTimeout)));
    }

    private String issueRefreshToken(User user) {
        Instant issuedAt = Instant.now();
        return tokenService.generate(new UserAuthenticationToken(user, issuedAt, issuedAt.plusMillis(accessKeyTimeout)) {
            @Override
            public List<GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }
        });
    }

}
