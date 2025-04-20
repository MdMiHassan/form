package mdmihassan.form.service.impl;

import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.SecurityContextHelper;
import mdmihassan.form.entity.User;
import mdmihassan.form.model.UserRegistrationRequest;
import mdmihassan.form.repository.UserRepository;
import mdmihassan.form.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User register(UserRegistrationRequest userRegistrationRequest) {
        return null;
    }

    @Override
    public Optional<User> get(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHelper.getAuthentication().getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user '" + username + "' not exists"));
    }

}
