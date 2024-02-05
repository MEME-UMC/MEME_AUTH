package umc.meme.auth.global.oauth.jwk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JWKRepository extends JpaRepository<JWK, Long> {
    Optional<JWK> findByKid(String kid);
}
