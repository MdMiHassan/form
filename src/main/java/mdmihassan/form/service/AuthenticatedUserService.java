package mdmihassan.form.service;

import mdmihassan.form.entity.User;

import java.util.Optional;

public interface AuthenticatedUserService {

    Optional<User> loadAuthenticatedUser();

}
