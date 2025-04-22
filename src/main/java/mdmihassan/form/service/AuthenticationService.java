package mdmihassan.form.service;

import mdmihassan.form.model.AccessTokenRequest;
import mdmihassan.form.model.AuthenticationResponse;
import mdmihassan.form.model.LoginRequest;

public interface AuthenticationService {

    AuthenticationResponse login(LoginRequest loginRequest);

    AuthenticationResponse getAccessToken(AccessTokenRequest accessTokenRequest);

}
