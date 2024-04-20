package umc.meme.auth.global.auth.converter;

import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.global.auth.dto.AuthResponse;

public class TokenConverter {

    public static AuthResponse.JoinDto toJoinDto(User user, String[] tokenPair, String role) {
        return AuthResponse.JoinDto.builder()
                .access_token(tokenPair[0])
                .refresh_token(tokenPair[1])
                .user_id(user.getUserId())
                .details(user.getDetails())
                .role(role)
                .build();
    }

    public static AuthResponse.TokenDto toTokenDto(String[] tokenPair) {
        return AuthResponse.TokenDto.builder()
                .access_token(tokenPair[0])
                .refresh_token(tokenPair[1])
                .build();
    }
}
