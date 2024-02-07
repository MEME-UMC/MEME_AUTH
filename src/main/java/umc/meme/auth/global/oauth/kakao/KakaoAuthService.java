package umc.meme.auth.global.oauth.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.domain.user.entity.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.JwtHandler;
import umc.meme.auth.global.oauth.jwk.JWK;
import umc.meme.auth.global.oauth.jwk.JWKRepository;

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
        String userEmail = validateIdToken(idToken);

        if (userEmail == null)
            throw new ValidationException("Validation Exception");

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Email not found: " + userEmail));
    }

    private String validateIdToken(String idToken) {
        // ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String header = getHeader(idToken);
        // 헤더를 Base64 방식으로 디코딩
        String decodedHeader = new String(Decoders.BASE64.decode(header));

        JsonParser parser = new JsonParser();
        // JsonElement element = parser.parse(decodedHeader.toString());
        JsonElement element = JsonParser.parseString(decodedHeader.toString());

        String kid = element.getAsJsonObject().get("kid").getAsString();
        JWK publicKey = keyRepository.findByKid(kid)
                .orElseThrow(() -> new JwtHandler(NO_PUBLIC_KEY_EXCEPTION));

        String userEmail = null;
        try {
            Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(publicKey.getKty(), publicKey.getN(), publicKey.getE()))
                    .requireIssuer(issuer)
                    .requireAudience(restApiKey)
                    .build()
                    .parseClaimsJws(idToken);
            userEmail = parseClaimsJws.getBody().get("email").toString();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {  // Signing Key 검증
            throw new JwtHandler(ErrorStatus.NO_PUBLIC_KEY_EXCEPTION);
        } catch (SignatureException | MalformedJwtException e) {  // Signature 검증
            throw new JwtHandler(ErrorStatus.INVALID_SIGNATURE_EXCEPTION);
        } catch (MissingClaimException | IncorrectClaimException e) {  // Payload 검증 (iss, aud)
            throw new JwtHandler(ErrorStatus.JWT_PAYLOAD_EXCEPTION);
        } catch (ExpiredJwtException e) {  // Payload 검증 (exp)
            e.printStackTrace();
        }

        return userEmail;
    }

    private String getHeader(String idToken) {
        String[] splitToken = splitToken(idToken);
        return splitToken[0];
    }

    private String[] splitToken(String idToken) {
        String[] splitToken = idToken.split("\\.");
        if (splitToken.length != 3)
            throw new JwtHandler(ErrorStatus.JWT_TOKEN_INVALID);
        return splitToken;
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
