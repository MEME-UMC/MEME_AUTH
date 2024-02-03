package umc.meme.auth.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthRequest {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto {
        private String username;
        private String email;
//        private String accessToken;
    }
}
