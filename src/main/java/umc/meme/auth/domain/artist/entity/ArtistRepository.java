package umc.meme.auth.domain.artist.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    boolean existsByNickName(String nickName);

}
