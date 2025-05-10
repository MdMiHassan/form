package mdmihassan.form.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mdmihassan.form.model.APIResponse;
import mdmihassan.form.model.UserRegistrationRequest;
import mdmihassan.form.model.UserRegistrationResponse;
import mdmihassan.form.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public APIResponse<UserRegistrationResponse> register(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        return APIResponse.ok(userService.registerUser(userRegistrationRequest), "User registration successful");
    }

}
