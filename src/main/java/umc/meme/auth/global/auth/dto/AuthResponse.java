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
        private String access_token;
        private String refresh_token;
        private Long user_id;
        private boolean details;
        private String type;

        public TokenDto(String access_token, String refresh_token) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
        }

        public void setUser_id(Long userId) {
            this.user_id = userId;
        }

        public void setDetails(boolean details) {
            this.details = details;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String access_token;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private String access_token;
        private String refresh_token;
        private boolean isUser;
        private Long user_id;
        private String role;
    }
}
