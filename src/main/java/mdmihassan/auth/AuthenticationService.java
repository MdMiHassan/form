package mdmihassan.auth;

import mdmihassan.auth.token.AccessTokenRequest;

public interface AuthenticationService {

    AuthenticationResponse login(LoginRequest loginRequest);

    AuthenticationResponse getAccessToken(AccessTokenRequest accessTokenRequest);

}
