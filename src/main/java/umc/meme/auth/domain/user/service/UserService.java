package umc.meme.auth.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.domain.user.dto.UserRequest;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void signup(UserRequest.JoinDto joinDto) {
        if (joinDto.getRole() == "ARTIST") {
            userRepository.saveArtist(joinDto.getUsername(), joinDto.getEmail());
        } else {
            userRepository.saveModel(joinDto.getUsername(), joinDto.getEmail());
        }
    }
}
