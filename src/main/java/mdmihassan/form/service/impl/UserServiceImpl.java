package mdmihassan.form.service.impl;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.SecurityContextHelper;
import mdmihassan.form.dto.UserDto;
import mdmihassan.form.entity.User;
import mdmihassan.form.exception.DuplicateResourceException;
import mdmihassan.form.model.UserRegistrationRequest;
import mdmihassan.form.repository.UserRepository;
import mdmihassan.form.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationRequest userRegistrationRequest) {
        UserDto userDto = userRegistrationRequest.getUser();

        boolean userExists = userRepository.existsByEmailOrUsername(userDto.getEmail(), userDto.getUsername());
        if (userExists) {
            throw new DuplicateResourceException("user exists with the provided email or username");
        }

        User user = new User();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true);
        user.setLocked(false);

        getAuthenticatedUser().ifPresent(authUser -> {
            if (authUser.hasRole(User.Role.ADMIN)) {
                user.addRoles(userDto.getRoles());
                user.setAccountExpiration(userDto.getAccountExpiration());
            }
        });

        User registerdUser = userRepository.save(user);
        registerdUser.setPassword(null);
        return registerdUser;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHelper.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user '" + username + "' not exists"));
    }

}
