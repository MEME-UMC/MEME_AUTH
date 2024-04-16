package umc.meme.auth.global.oauth.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.AuthException;
import umc.meme.auth.global.oauth.jsonwebkey.JsonWebKey;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Service
public abstract class OAuthService {

    @Transactional
    public String getUserInfo(String idToken) throws AuthException {
        String userEmail = null;

        try {
            // Provider에 맞는 Json Web Key 리스트 가져오기
            String keyString = getJsonWebKeys();  // 이 부분
            List<JsonWebKey> jsonWebKeys = parseKeys(keyString);
            // id token 헤더에서 kid 값 가져오기
            String kid = getKidFromToken(idToken);

            // 일치하는 kid 값 가져오기
            JsonWebKey selectedKey = null;
            for (JsonWebKey jsonWebKey : jsonWebKeys) {
                if (kid.equals(jsonWebKey.getKid()))
                    selectedKey = jsonWebKey;
            }

            if (selectedKey == null)
                throw new AuthException(ErrorStatus.KEY_NOT_FOUND);

            // 서명 검증
            Claims claims = validateSignature(idToken, getRSAPublicKey(selectedKey));
            userEmail = claims.get("email").toString();
        } catch (IOException exception) {
            throw new AuthException(ErrorStatus.NOT_FOUND);
        } catch (GeneralSecurityException exception) {
            throw new AuthException(ErrorStatus.GENERAL_SECURITY_EXCEPTION);
        } catch (AuthException authException) {
            throw authException;
        }

        return userEmail;
    }

    private String getKidFromToken(String idToken) throws AuthException {
        // ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String header = getHeader(idToken);
        // 헤더를 Base64 방식으로 디코딩
        String decodedHeader = new String(Decoders.BASE64.decode(header));
        // JsonParser 사용하여 헤더에 있는 kid 값 불러오기
        JsonElement element = JsonParser.parseString(decodedHeader.toString());
        return element.getAsJsonObject().get("kid").getAsString();
    }

    private String getHeader(String idToken) throws AuthException {
        String[] dividedToken = splitToken(idToken);
        return dividedToken[0];
    }

    private String[] splitToken(String idToken) throws AuthException {
        String[] dividedToken = idToken.split("\\.");
        if (dividedToken.length != 3)
            throw new AuthException(ErrorStatus.JWT_TOKEN_INVALID);
        return dividedToken;
    }

    private Key getRSAPublicKey(JsonWebKey selectedKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance(selectedKey.getKty());
        byte[] decodeM = Base64.getUrlDecoder().decode(selectedKey.getN());
        byte[] decodeE = Base64.getUrlDecoder().decode(selectedKey.getE());
        BigInteger m = new BigInteger(1, decodeM);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(m, e);
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }

    private List<JsonWebKey> parseKeys(String keyString) {
        // JsonParser 사용해서 공개 키 목록 배열 추출
        JsonElement jsonElement = JsonParser.parseString(keyString);
        JsonArray jsonArray = jsonElement.getAsJsonObject().get("keys").getAsJsonArray();

        // 파싱 진행하면 해당 키 객체 리스트로 넣기
        List<JsonWebKey> keys = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            keys.add(JsonWebKey.builder()
                    .kid(element.getAsJsonObject().get("kid").toString().replace("\"", ""))
                    .kty(element.getAsJsonObject().get("kty").toString().replace("\"", ""))
                    .alg(element.getAsJsonObject().get("alg").toString().replace("\"", ""))
                    .use(element.getAsJsonObject().get("use").toString().replace("\"", ""))
                    .n(element.getAsJsonObject().get("n").toString().replace("\"", ""))
                    .e(element.getAsJsonObject().get("e").toString().replace("\"", ""))
                    .build());
        }
        return keys;
    }

    protected abstract String getJsonWebKeys() throws IOException;
    protected abstract Claims validateSignature(String idToken, Key signingKey) throws AuthException;
}
