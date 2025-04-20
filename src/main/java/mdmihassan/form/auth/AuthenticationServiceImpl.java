package mdmihassan.form.auth;

import lombok.extern.slf4j.Slf4j;
import mdmihassan.form.entity.User;
import mdmihassan.form.model.AccessTokenRequest;
import mdmihassan.form.model.AuthenticationResponse;
import mdmihassan.form.model.LoginRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${form.security.api.key.refresh.timeout}")
    private long refreshKeyTimeout;

    @Value("${form.security.api.key.access.timeout}")
    private long accessKeyTimeout;

    public AuthenticationServiceImpl(@Qualifier("jwtTokenService") TokenService tokenService,
                                     AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Incorrect Username or Password");
        }
        User user = (User) authentication.getPrincipal();
        return AuthenticationResponse.of(generateRefreshToken(user), generateAccessToken(user));
    }

    @Override
    public AuthenticationResponse getAccessToken(AccessTokenRequest accessTokenRequest) {
        UserAuthorityToken userAuthorityToken = tokenService.verify(accessTokenRequest.getRefreshToken());
        if (!userAuthorityToken.isAuthenticated()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return AuthenticationResponse.of(accessTokenRequest.getRefreshToken(),
                generateAccessToken(userAuthorityToken.getUser()));
    }

    private String generateAccessToken(User user) {
        return tokenService.generate(
                TimeoutToken.fromNow(true, user, accessKeyTimeout, user.getAuthorities()));
    }

    private String generateRefreshToken(User user) {
        return tokenService.generate(
                TimeoutToken.fromNow(true, user, refreshKeyTimeout, Collections.emptyList()));
    }

}
