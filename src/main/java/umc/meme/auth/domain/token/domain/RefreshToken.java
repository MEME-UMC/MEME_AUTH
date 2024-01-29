package umc.meme.auth.domain.token.domain;

import jakarta.persistence.Id;
import lombok.Builder;

import java.io.Serializable;

@Builder
public class RefreshToken implements Serializable {

    @Id
    private String refreshToken;
    private String accessToken;

    public RefreshToken(final String refreshToken, final String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
