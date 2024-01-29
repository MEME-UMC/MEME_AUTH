package umc.meme.auth.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RefreshRequest {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenDto {
        private String refreshToken;
    }
}
