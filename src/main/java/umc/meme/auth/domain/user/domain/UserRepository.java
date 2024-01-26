package umc.meme.auth.domain.user.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "insert into artist values (username = :username, email = :email) ", nativeQuery = true)
    void saveArtist(@Param("username") String username, @Param("email") String email);

    @Query(value = "insert into model values (username = :username, email = :email) ", nativeQuery = true)
    void saveModel(@Param("username") String username, @Param("email") String email);
}
