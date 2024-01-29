package umc.meme.auth.domain.token.domain;

import jakarta.persistence.Id;

import java.io.Serializable;

//@RedisHash(value = "refreshToken", timeToLive = 60)
public class RefreshToken implements Serializable {

    @Id
    private String refreshToken;
    private Long memberId;

    public RefreshToken(final String refreshToken, final Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getMemberId() {
        return memberId;
    }
}
