package mdmihassan.user;

import lombok.RequiredArgsConstructor;
import mdmihassan.util.Preconditions;
import mdmihassan.verification.EmailVerificationService;
import mdmihassan.web.api.DuplicateResourceException;
import mdmihassan.web.api.UnauthorizedActionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, AuthenticatedUserService {

    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest) {
        UserDto userDto = userRegistrationRequest.getUser();

        if (userRepository.existsByPrimaryEmailOrUsername(userDto.getPrimaryEmail(), userDto.getUsername())) {
            throw new DuplicateResourceException("user exists with the provided email or username");
        }

        User user = new User();
        if (Preconditions.nonNullAndNonBlank(userRegistrationRequest.getAuthCode())) {
            if (!emailVerificationService.verifySecret(userDto.getPrimaryEmail(), userRegistrationRequest.getAuthCode())) {
                throw new UnauthorizedActionException("email verification failed");
            }
            user.primaryEmailVerified();
        } else {
            emailVerificationService.issueChallenge(userDto.getPrimaryEmail());
        }

        user.setFirstName(StringUtils.normalizeSpace(userDto.getFirstName()));
        user.setLastName(StringUtils.normalizeSpace(userDto.getLastName()));
        user.setUsername(userDto.getUsername());
        user.setPrimaryEmail(userDto.getPrimaryEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.enable();

        Optional<User> authenticatedUser = loadAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            User authUser = authenticatedUser.get();
            if (authUser.hasRole(User.Role.ADMIN)) {
                if (userDto.getAuthorities() != null) {
                    user.setAuthorities(userDto.getAuthorities());
                }
                user.setAccountExpiration(userDto.getAccountExpiration());
                user.setLocked(userDto.isLocked());
            }
        } else {
            user.setRoles(Set.of(User.Role.USER));
        }

        User registerdUser = userRepository.save(user);

        return UserRegistrationResponse.builder()
                .verified(registerdUser.isPrimaryEmailVerified())
                .user(UserDto.builder()
                        .id(registerdUser.getId())
                        .username(userDto.getUsername())
                        .primaryEmail(registerdUser.getPrimaryEmail())
                        .firstName(registerdUser.getFirstName())
                        .lastName(registerdUser.getLastName())
                        .enabled(registerdUser.isEnabled())
                        .locked(registerdUser.isLocked())
                        .accountExpiration(registerdUser.getAccountExpiration())
                        .credentialsExpiration(registerdUser.getCredentialsExpiration())
                        .authorities(registerdUser.getAuthorities())
                        .build())
                .build();
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> loadAuthenticatedUser() {
        return Optional.ofNullable(getAuthenticationUser());
    }

    private User getAuthenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return null;
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user '" + username + "' not exists"));
    }

}
