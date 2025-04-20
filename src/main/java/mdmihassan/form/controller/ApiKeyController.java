package mdmihassan.form.controller;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.ApiAccessToken;
import mdmihassan.form.auth.UserTokenService;
import mdmihassan.form.model.ApiKey;
import mdmihassan.form.model.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/api/secrets")
public class ApiKeyController {

    private final UserTokenService userTokenService;

    @PostMapping
    public ApiResponse<ApiKey> issueApiKey(@RequestBody ApiAccessToken apiAccessToken) {
        return ApiResponse.ok(userTokenService.issueKey(apiAccessToken), "API key issued successfully");
    }

    @GetMapping
    public ApiResponse<List<ApiKey>> getAllIssuedApiKeys() {
        return ApiResponse.ok(userTokenService.getAllIssuedKeys(), "API key issued by this user");
    }

}
