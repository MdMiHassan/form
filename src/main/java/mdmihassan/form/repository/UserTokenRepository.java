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

    List<UserToken> findAllByIdInAndUser(List<UUID> tokenIds, User currentUser);

    void deleteAllByUser(User user);

    UserToken findByIdAndUser(UUID tokenId, User user);

    void deleteAllByIdInAndUser(List<UUID> tokenIds, User user);

}
