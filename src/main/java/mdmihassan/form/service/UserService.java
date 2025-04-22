package mdmihassan.form.service;

import mdmihassan.form.entity.User;
import mdmihassan.form.model.UserRegistrationRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    User registerUser(UserRegistrationRequest userRegistrationRequest);

    Optional<User> getUser(UUID id);

    User getCurrentUser();

}
