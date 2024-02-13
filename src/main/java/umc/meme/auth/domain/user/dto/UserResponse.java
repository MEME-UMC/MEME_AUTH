package umc.meme.auth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponse {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinSuccessDto {
        private Long userId;
    }
}
