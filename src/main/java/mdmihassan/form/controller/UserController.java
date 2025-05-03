package mdmihassan.form.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mdmihassan.form.dto.UserDto;
import mdmihassan.form.model.APIResponse;
import mdmihassan.form.model.UserRegistrationRequestModel;
import mdmihassan.form.model.UserRegistrationResponseModel;
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
    public APIResponse<UserRegistrationResponseModel> register(@RequestBody @Valid UserRegistrationRequestModel userRegistrationRequestModel) {
        return APIResponse.ok(userService.registerUser(userRegistrationRequestModel), "User registration successful");
    }

}
