package umc.meme.auth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDto {
        private String username;
        private String email;
        private String role;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto {
        private String username;
        private String email;
    }
}
