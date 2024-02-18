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
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private boolean details;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setDetails(boolean details) {
            this.details = details;
        }
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String accessToken;
    }
}
