package umc.meme.auth.global.oauth.jsonwebkey;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class PublicKeyDto implements Serializable {

    private String provider;
    private String key;

    public PublicKeyDto(String provider, String key) {
        this.provider = provider;
        this.key = key;
    }

    public String getProvider() {
        return provider;
    }

    public String getKey() {
        return key;
    }
}
