package mdmihassan.form.service;

import mdmihassan.form.entity.User;
import mdmihassan.form.model.UserRegistrationRequest;
import mdmihassan.form.model.UserRegistrationResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest);

    Optional<User> getUser(UUID id);

}
