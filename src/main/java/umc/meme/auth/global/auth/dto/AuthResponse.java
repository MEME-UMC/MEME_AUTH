package umc.meme.auth.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthResponse {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String accessToken;
    }
}
