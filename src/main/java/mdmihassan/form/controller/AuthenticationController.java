package mdmihassan.form.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.GrantedAuthorityImpl;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.model.*;
import mdmihassan.form.service.AuthenticationService;
import mdmihassan.form.service.UserTokenService;
import mdmihassan.form.util.ValueWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserTokenService userTokenService;

    @PostMapping("/users/login")
    public APIResponse<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return APIResponse.ok(authenticationService.login(loginRequest), "Login successful");
    }

    @PostMapping("/users/access/tokens")
    public APIResponse<AuthenticationResponse> getAccessToken(@RequestBody @Valid AccessTokenRequest accessTokenRequest) {
        return APIResponse.ok(authenticationService.getAccessToken(accessTokenRequest), "Access key issued");
    }

    @PostMapping("/api/secrets")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<UserTokenDto> issueUserToken(@RequestBody @Valid UserTokenDto userTokenDto) {
        return APIResponse.ok(userTokenService.issueUserToken(userTokenDto), "API key issued successfully");
    }

    @GetMapping("/api/secrets")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<List<UserTokenDto>> getAllIssuedUserTokens() {
        return APIResponse.ok(userTokenService.getAllIssuedTokens(), "Issued API keys");
    }

    @DeleteMapping("/api/secrets")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> deleteAllIssuedUserTokens() {
        userTokenService.deleteAll();
        return APIResponse.ok("All issued API keys deleted successfully");
    }

    @PostMapping("/api/secrets/invalidate")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> invalidateAllIssuedUserTokens() {
        userTokenService.invalidateAll();
        return APIResponse.ok("All issued API keys invalidated");
    }

    @PutMapping("/api/secrets/{tokenId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<UserTokenDto> updateUserToken(@PathVariable UUID tokenId,
                                                     @RequestBody @Valid UserTokenUpdateRequestModel userTokenUpdateRequestModel) {
        return APIResponse.ok(userTokenService.update(tokenId, userTokenUpdateRequestModel),
                "API key updated");
    }

    @DeleteMapping("/api/secrets/{tokenId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> deleteUserToken(@PathVariable UUID tokenId) {
        userTokenService.delete(tokenId);
        return APIResponse.ok("API key deleted");
    }

    @PostMapping("/api/secrets/{tokenId}/invalidate")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> invalidateUserToken(@PathVariable UUID tokenId) {
        userTokenService.invalidate(tokenId);
        return APIResponse.ok("API key invalidated");
    }

    @PutMapping("/api/secrets/{tokenId}/authorities")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> setUserTokenAuthorities(@PathVariable UUID tokenId, @Valid @RequestBody ValueWrapper<List<GrantedAuthorityImpl>> authorities) {
        userTokenService.setAuthorities(tokenId, authorities.getValue());
        return APIResponse.ok("API key authorization updated successfully");
    }

    @PatchMapping("/api/secrets/{tokenId}/authorities")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> addUserTokenAuthorities(@PathVariable UUID tokenId, @Valid @RequestBody ValueWrapper<List<GrantedAuthorityImpl>> authorities) {
        userTokenService.addAuthorities(tokenId, authorities.getValue());
        return APIResponse.ok("Authorizations added to the API key");
    }

    @PutMapping("/api/secrets/{tokenId}/expiration")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public APIResponse<?> changeUserTokenExpiration(@PathVariable UUID tokenId, @RequestBody ValueWrapper<Instant> expiration) {
        userTokenService.setExpiration(tokenId, expiration.getValue());
        return APIResponse.ok("API key expiration updated");
    }

}
