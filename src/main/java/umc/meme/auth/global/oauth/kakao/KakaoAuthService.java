package umc.meme.auth.global.oauth.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.JwtHandler;
import umc.meme.auth.global.oauth.kakao.jwk.JWK;
import umc.meme.auth.global.oauth.kakao.jwk.JWKRepository;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static umc.meme.auth.global.common.status.ErrorStatus.NO_PUBLIC_KEY_EXCEPTION;

@RequiredArgsConstructor
@Service
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JWKRepository keyRepository;

    @Value("${spring.security.oauth2.kakao.issuer}")
    private String issuer;

    @Value("${spring.security.oauth2.kakao.rest-api-key}")
    private String restApiKey;

    @Transactional
    public User getUserInfo(String idToken) {

        if (!validateIdToken(idToken))
            throw new ValidationException("Validation Exception");

        String payload = getPayload(idToken);
        String decodedPayload = new String(Decoders.BASE64.decode(payload));

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(decodedPayload.toString());

        String email = element.getAsJsonObject().get("email").getAsString();
        System.out.println("email = " + email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email not found: " + email));
    }

    private boolean validateIdToken(String idToken) {
        // ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String payload = getPayload(idToken);
        String header = getHeader(idToken);

        if (validatePayload(payload) && validateSignature(header, idToken))
            return true;

        return false;
    }

    private String getHeader(String idToken) {
        String[] splitToken = splitToken(idToken);
        return splitToken[0];
    }

    private String getPayload(String idToken) {
        String[] splitToken = splitToken(idToken);
        return splitToken[1];
    }

    private String[] splitToken(String idToken) {
        String[] splitToken = idToken.split("\\.");
        if (splitToken.length != 3)
            throw new JwtHandler(ErrorStatus.JWT_TOKEN_INVALID);
        return splitToken;
    }

    private boolean validatePayload(String payload) {
        // 페이로드를 Base64 방식으로 디코딩
        String decodedPayload = new String(Decoders.BASE64.decode(payload));

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(decodedPayload.toString());

        String iss = element.getAsJsonObject().get("iss").getAsString();
        String aud = element.getAsJsonObject().get("aud").getAsString();
        String exp = element.getAsJsonObject().get("exp").getAsString();

        // 페이로드의 iss 값이 https://kauth.kakao.com 와 일치하는지 확인
        if (!iss.equals(issuer))
            throw new JwtHandler(ErrorStatus.JWT_PAYLOAD_EXCEPTION);

        // 페이로드의 aud 값이 서비스 앱 키와 일치하는지 확인
        if (!aud.equals(restApiKey))
            throw new JwtHandler(ErrorStatus.JWT_PAYLOAD_EXCEPTION);

        // 페이로드의 exp 값이 현재 UNIX 타임스탬프(Timestamp)보다 큰 값인지 확인(ID 토큰이 만료되지 않았는지 확인)
        if (Long.parseLong(exp) < System.currentTimeMillis() / 1000)
            throw new JwtHandler(ErrorStatus.JWT_PAYLOAD_EXCEPTION);

        return true;
    }

    private boolean validateSignature(String header, String idToken) {
        // 헤더를 Base64 방식으로 디코딩
        String decodedHeader = new String(Decoders.BASE64.decode(header));

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(decodedHeader.toString());

        String kid = element.getAsJsonObject().get("kid").getAsString();
        JWK publicKey = keyRepository.findByKid(kid)
                .orElseThrow(() -> new JwtHandler(NO_PUBLIC_KEY_EXCEPTION));

        try {
            Key rsaPublicKey = getRSAPublicKey(publicKey.getKty(), publicKey.getN(), publicKey.getE());
            Jwts.parserBuilder()
                    .setSigningKey(rsaPublicKey)
                    .build()
                    .parseClaimsJws(idToken);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return true;
    }

    private Key getRSAPublicKey(String kty, String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(kty);
        byte[] decodeM = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
        BigInteger m = new BigInteger(1, decodeM);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(m, e);
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }
}
