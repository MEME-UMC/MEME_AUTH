package umc.meme.auth.domain.user.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Query(value = "insert into Artist values (username = :username, email = :email) ", nativeQuery = true)
    void saveArtist(@Param("username") String username, @Param("email") String email);

    @Modifying
    @Query(value = "insert into Aodel values (username = :username, email = :email) ", nativeQuery = true)
    void saveModel(@Param("username") String username, @Param("email") String email);

    @Query(value = "select a from Artist a where a.username = :username")
    void findArtistByUsername(@Param("username") String username);

    @Query(value = "select m from Model m where m.username = :username")
    void findModelByUsername(@Param("username") String username);
}
