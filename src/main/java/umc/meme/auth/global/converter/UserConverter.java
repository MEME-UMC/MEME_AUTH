package umc.meme.auth.global.converter;

import umc.meme.auth.domain.artist.entity.Artist;
import umc.meme.auth.domain.model.entity.Model;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.config.SecurityConfig;

public class UserConverter {

    public static Model toModel(AuthRequest.ModelJoinDto modelJoinDto, String userEmail, String role) {
        return Model.builder()
                .role(role)
                .email(userEmail)
                .provider(modelJoinDto.getProvider())
                .profileImg(modelJoinDto.getProfile_img())
                .username(modelJoinDto.getUsername())
                .nickname(modelJoinDto.getNickname())
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .details(true)
                .gender(modelJoinDto.getGender())
                .skinType(modelJoinDto.getSkin_type())
                .personalColor(modelJoinDto.getPersonal_color())
                .build();
    }

    public static Artist toArtist(AuthRequest.ArtistJoinDto artistJoinDto, String userEmail, String role) {
        return Artist.builder()
                .role(role)
                .email(userEmail)
                .provider(artistJoinDto.getProvider())
                .profileImg(artistJoinDto.getProfile_img())
                .username(artistJoinDto.getUsername())
                .nickname(artistJoinDto.getNickname())
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .details(false)
                .build();
    }

    public static AuthResponse.UserInfoDto toUserInfoDtoExists(User user, String[] tokenPair) {
        return AuthResponse.UserInfoDto.builder()
                .access_token(tokenPair[0])
                .refresh_token(tokenPair[1])
                .user_status(true)
                .user_id(user.getUserId())
                .role(user.getRole())
                .build();
    }

    public static AuthResponse.UserInfoDto toUserInfoDtoNonExists() {
        return AuthResponse.UserInfoDto.builder()
                .user_status(false)
                .build();
    }
}
