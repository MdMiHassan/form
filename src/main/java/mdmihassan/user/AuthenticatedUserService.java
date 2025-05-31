package mdmihassan.user;

import java.util.Optional;

public interface AuthenticatedUserService {

    Optional<User> loadAuthenticatedUser();

}
