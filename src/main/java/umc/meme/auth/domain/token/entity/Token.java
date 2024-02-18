package umc.meme.auth.domain.token.entity;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class Token implements Serializable {

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
