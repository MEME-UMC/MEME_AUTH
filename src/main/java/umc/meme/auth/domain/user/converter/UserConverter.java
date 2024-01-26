package umc.meme.auth.domain.user.converter;

import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.dto.UserRequest;

import static umc.meme.auth.global.config.SecurityConfig.passwordEncoder;

public class UserConverter {

    public static User toUserEntity(UserRequest.JoinDto userJoin) {
        return User.builder()
                .username(userJoin.getUsername())
                .email(userJoin.getEmail())
                .password(passwordEncoder().encode(userJoin.getEmail()))
                .role(userJoin.getRole())
                .build();
    }
}
