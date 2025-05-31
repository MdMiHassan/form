package mdmihassan.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest);

    Optional<User> getUser(UUID id);

}
