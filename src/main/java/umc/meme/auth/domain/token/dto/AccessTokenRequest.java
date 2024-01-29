package umc.meme.auth.domain.token.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenRequest {
    @NotNull
    private String refreshToken;
}