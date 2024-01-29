package umc.meme.auth.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.domain.Artist;
import umc.meme.auth.domain.user.domain.ArtistRepository;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.domain.user.dto.UserRequest;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserRequest.JoinDto joinDto) {
        if (joinDto.getRole().startsWith("ARTIST") ){
            Artist user = Artist.builder()
                    .email(joinDto.getEmail())
                    .username(joinDto.getUsername())
                    .password(passwordEncoder.encode(joinDto.getEmail()))
                    .role("ARTIST")
                    .build();
            artistRepository.save(user);
        } else {
            userRepository.saveModel(joinDto.getUsername(), joinDto.getEmail());
        }
    }
}
