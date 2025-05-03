package mdmihassan.form.service;

import mdmihassan.form.entity.User;
import mdmihassan.form.model.UserRegistrationRequestModel;
import mdmihassan.form.model.UserRegistrationResponseModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserRegistrationResponseModel registerUser(UserRegistrationRequestModel userRegistrationRequestModel);

    Optional<User> getUser(UUID id);

}
