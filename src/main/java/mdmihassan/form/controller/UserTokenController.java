package mdmihassan.form.controller;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.UserTokenAuthentication;
import mdmihassan.form.service.UserTokenService;
import mdmihassan.form.dto.UserTokenDto;
import mdmihassan.form.model.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/api/secrets")
public class UserTokenController {

    private final UserTokenService userTokenService;

    @PostMapping
    public ApiResponse<UserTokenDto> issueUserToken(@RequestBody UserTokenAuthentication apiAccessToken) {
        return ApiResponse.ok(userTokenService.issueUserToken(apiAccessToken), "API key issued successfully");
    }

    @GetMapping
    public ApiResponse<List<UserTokenDto>> getAllIssuedUserTokens() {
        return ApiResponse.ok(userTokenService.getAllIssuedTokens(), "API key issued by this user");
    }

}
