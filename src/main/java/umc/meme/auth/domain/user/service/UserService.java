package umc.meme.auth.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.artist.domain.Artist;
import umc.meme.auth.domain.artist.domain.ArtistRepository;
import umc.meme.auth.domain.artist.domain.enums.Gender;
import umc.meme.auth.domain.model.domain.Model;
import umc.meme.auth.domain.model.domain.ModelRepository;
import umc.meme.auth.domain.model.domain.enums.PersonalColor;
import umc.meme.auth.domain.model.domain.enums.SkinType;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.MemberHandler;

import java.util.Optional;

import static umc.meme.auth.global.config.SecurityConfig.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final ArtistRepository artistRepository;
    private final ModelRepository modelRepository;

    @Transactional
    public void modelSignUp(UserRequest.modelJoinDto joinDto) {
        modelRepository.save(Model.builder()
                .email(joinDto.getEmail())
                .username(joinDto.getUsername())
                .password(passwordEncoder().encode(joinDto.getEmail()))
                .role("MODEL")
                .gender(Gender.valueOf(joinDto.getGender()))
                .nickname(joinDto.getNickname())
                .profileImg(joinDto.getProfileSrc())
                .skinType(SkinType.valueOf(joinDto.getSkinType()))
                .personalColor(PersonalColor.valueOf(joinDto.getPersonalColor()))
                .build());
    }

    @Transactional
    public void artistSignUp(UserRequest.artistJoinDto joinDto) {
        artistRepository.save(Artist.builder()
                .email(joinDto.getEmail())
                .username(joinDto.getUsername())
                .password(passwordEncoder().encode(joinDto.getEmail()))
                .role("ARTIST")
                .nickname(joinDto.getNickname())
                .profileSrc(joinDto.getProfileSrc())
                .build());
    }

    @Transactional
    public void artistExtra(UserRequest.artistExtraDto joinDto) {
        Artist artist = artistRepository.findById(joinDto.getArtistId()).orElseThrow(() -> new MemberHandler(ErrorStatus.ARTIST_NOT_FOUND));
        artist.update(joinDto);
    }

//    @Transactional
//    public void signup(UserRequest.JoinDto joinDto) {
//        switch (joinDto.getRole()) {
//            case "ARTIST":
//                artistRepository.save(Artist.builder()
//                        .email(joinDto.getEmail())
//                        .username(joinDto.getUsername())
//                        .password(passwordEncoder().encode(joinDto.getEmail()))
//                        .role("ARTIST")
//                        .build());
//                break;
//            case "MODEL":
//                modelRepository.save(Model.builder()
//                        .email(joinDto.getEmail())
//                        .username(joinDto.getUsername())
//                        .password(passwordEncoder().encode(joinDto.getEmail()))
//                        .role("model")
//                        .build());
//                break;
//            default:
//                throw new IllegalArgumentException("ROLE_TYPE_MISMATCH");
//        }
//    }

}
