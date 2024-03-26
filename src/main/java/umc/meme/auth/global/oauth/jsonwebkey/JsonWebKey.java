package umc.meme.auth.global.oauth.jsonwebkey;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonWebKey {

    // 싱글톤은 어때
    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;

    @Builder
    public JsonWebKey(String kid, String kty, String alg, String use, String n, String e) {
        this.kid = kid;
        this.kty = kty;
        this.alg = alg;
        this.use = use;
        this.n = n;
        this.e = e;
    }

    public String getKid() {
        return kid;
    }

    public String getKty() {
        return kty;
    }

    public String getAlg() {
        return alg;
    }

    public String getUse() {
        return use;
    }

    public String getN() {
        return n;
    }

    public String getE() {
        return e;
    }
}
