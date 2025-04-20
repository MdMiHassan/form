package mdmihassan.form.repository;

import mdmihassan.form.entity.User;
import mdmihassan.form.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByTokenHash(String tokenHash);

    List<UserToken> findAllByUserId(UUID id);

    List<UserToken> findAllByUser(User user);

    List<UserToken> findAllByUserIdAndIdIn(User currentUser, List<UUID> tokenIds);

    void deleteAllByUser(User user);

    void deleteAllByUserAndIdIn(User user, List<UUID> tokenIds);
}
