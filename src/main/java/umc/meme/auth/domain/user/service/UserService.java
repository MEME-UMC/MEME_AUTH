package umc.meme.auth.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.artist.domain.Artist;
import umc.meme.auth.domain.artist.domain.ArtistRepository;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.domain.user.dto.UserRequest;

import static umc.meme.auth.global.config.SecurityConfig.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    @Transactional
    public void signup(UserRequest.JoinDto joinDto) {
        switch (joinDto.getRole()) {
            case "ARTIST":
                artistRepository.save(Artist.builder()
                        .email(joinDto.getEmail())
                        .username(joinDto.getUsername())
                        .password(passwordEncoder().encode(joinDto.getEmail()))
                        .role("ARTIST")
                        .build());
                break;
            case "MODEL":
                // userRepository.saveModel(joinDto.getUsername(), joinDto.getEmail());
                break;
            default:
                throw new IllegalArgumentException("ROLE_TYPE_MISMATCH");
        }
    }
}
