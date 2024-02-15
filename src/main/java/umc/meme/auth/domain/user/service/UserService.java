package umc.meme.auth.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.artist.entity.Artist;
import umc.meme.auth.domain.artist.entity.ArtistRepository;
import umc.meme.auth.domain.model.entity.Model;
import umc.meme.auth.domain.model.entity.ModelRepository;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.domain.user.dto.UserResponse;
import umc.meme.auth.global.common.status.ErrorStatus;

import java.time.LocalDate;

import static umc.meme.auth.global.config.SecurityConfig.*;
import static umc.meme.auth.global.enums.UserStatus.ACTIVE;

@RequiredArgsConstructor
@Service
public class UserService {

    private final ArtistRepository artistRepository;
    private final ModelRepository modelRepository;

    @Transactional
    public UserResponse.JoinSuccessDto modelSignUp(UserRequest.ModelJoinDto joinDto) {
        Long userId = modelRepository.save(Model.builder()
                .email(joinDto.getEmail())
                .provider(joinDto.getProvider())
                .profileImg(joinDto.getProfileImg())
                .username(joinDto.getUsername())
                .nickname(joinDto.getNickname())
                .gender(joinDto.getGender())
                .skinType(joinDto.getSkinType())
                .personalColor(joinDto.getPersonalColor())
                .password(passwordEncoder().encode(joinDto.getEmail()))
                .role("MODEL")
                .userStatus(ACTIVE)
                .inactiveDate(LocalDate.of(2099, 12, 31))
                .build()).getUserId();

        return new UserResponse.JoinSuccessDto(userId);
    }

    @Transactional
    public UserResponse.JoinSuccessDto artistSignUp(UserRequest.ArtistJoinDto joinDto) {
        Long userId = artistRepository.save(Artist.builder()
                .email(joinDto.getEmail())
                .provider(joinDto.getProvider())
                .profileImg(joinDto.getProfileImg())
                .username(joinDto.getUsername())
                .nickname(joinDto.getNickname())
                .password(passwordEncoder().encode(joinDto.getEmail()))
                .role("ARTIST")
                .userStatus(ACTIVE)
                .inactiveDate(LocalDate.of(2099, 12, 31))
                .build()).getUserId();

        return new UserResponse.JoinSuccessDto(userId);
    }

    @Transactional
    public void artistExtra(UserRequest.ArtistExtraDto joinDto) {
        Artist artist = artistRepository.findById(joinDto.getUserId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.ARTIST_NOT_FOUND));
        artist.update(joinDto);
    }
}
