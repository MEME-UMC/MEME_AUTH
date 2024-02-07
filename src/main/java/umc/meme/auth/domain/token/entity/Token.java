package umc.meme.auth.domain.token.entity;

import jakarta.persistence.Id;
import lombok.Builder;

import java.io.Serializable;

@Builder
public class Token implements Serializable {

    @Id
    private String accessToken;

    private String refreshToken;

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
