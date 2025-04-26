package mdmihassan.form.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.UserTokenAuthentication;
import mdmihassan.form.model.AuthenticationResponse;
import mdmihassan.form.model.LoginRequest;
import mdmihassan.form.service.AuthenticationService;
import mdmihassan.form.service.UserTokenService;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.model.APIResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserTokenService userTokenService;

    @PostMapping("/users/login")
    public APIResponse<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return APIResponse.ok(authenticationService.login(loginRequest), "User login successful");
    }

    @PostMapping("/api/secrets")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<UserTokenDto> issueUserToken(@RequestBody UserTokenAuthentication apiAccessToken) {
        return APIResponse.ok(userTokenService.issueUserToken(apiAccessToken), "API key issued successfully");
    }

    @GetMapping("/api/secrets")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<List<UserTokenDto>> getAllIssuedUserTokens() {
        return APIResponse.ok(userTokenService.getAllIssuedTokens(), "API keys issued by this user");
    }

}
