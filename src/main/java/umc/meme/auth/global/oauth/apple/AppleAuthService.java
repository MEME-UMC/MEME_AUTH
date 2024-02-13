package umc.meme.auth.global.oauth.apple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.domain.user.entity.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.AuthException;
import umc.meme.auth.global.exception.handler.JwtHandler;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.oauth.AuthService;
import umc.meme.auth.global.oauth.jwk.JsonWebKey;
import umc.meme.auth.global.oauth.jwk.PublicKeyDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppleAuthService {

    private static final String REQUEST_URL = "https://appleid.apple.com/auth/keys";
    private static final String PROVIDER = "APPLE";

    @Value("${spring.security.oauth2.apple.issuer}")
    private String issuer;

    @Value("${spring.security.oauth2.apple.client-id}")
    private String restApiKey;

    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    @Transactional
    public User getUserInfo(String idToken) throws AuthException {
        String userEmail = getUserEmail(idToken);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Email not found: " + userEmail));
    }

    private String getUserEmail(String idToken) {
        String userEmail = null;
        try {
            // Provider에 맞는 Json Web Key 리스트 가져오기
            List<JsonWebKey> jsonWebKeys = getJsonWebKeys();
            // id token 헤더에서 kid 값 가져오기
            String kid = getKidFromToken(idToken);

            // 일치하는 kid 값 가져오기
            JsonWebKey selectedKey = null;
            for (JsonWebKey jsonWebKey : jsonWebKeys) {
                if (kid.equals(jsonWebKey.getKid()))
                    selectedKey = jsonWebKey;
            }

            // 서명 검증
            Jws<Claims> parseClaimsJws = Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(selectedKey.getKty(), selectedKey.getN(), selectedKey.getE()))
                    .requireIssuer(issuer)
                    .requireAudience(restApiKey)
                    .build()
                    .parseClaimsJws(idToken);

            userEmail = parseClaimsJws.getBody().get("email").toString();
        } catch (UnsupportedJwtException e) {
            throw new AuthException(ErrorStatus.UNSUPPORTED_JWT_EXCEPTION);
        } catch (MalformedJwtException e) {
            throw new AuthException(ErrorStatus.MALFORMED_JWT_EXCEPTION);
        } catch (SignatureException e) {
            throw new AuthException(ErrorStatus.SIGNATURE_EXCEPTION);
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorStatus.EXPIRED_JWT_EXCEPTION);
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorStatus.ILLEGAL_ARGUMENT_EXCEPTION);
        } catch (GeneralSecurityException e) {
            throw new AuthException(ErrorStatus.GENERAL_SECURITY_EXCEPTION);
        } catch (IOException e) {
            throw new AuthException(ErrorStatus.NOT_FOUND);
        }

        return userEmail;
    }

    private List<JsonWebKey> getJsonWebKeys() throws IOException {
        // Redis 안에 캐시 값으로 카카오 OIDC 공개 키 목록이 저장되어 있는지 확인
        Optional<PublicKeyDto> kakaoPublicKeyDto = redisRepository.findPublicKey(PROVIDER);

        // 공개 키 목록이 저장되어 있지 않다면 GET 요청 보내서 공개 키 세팅 (공개 키 캐시 여부 확인)
        if (kakaoPublicKeyDto.get().getKey() == null)
            setPublicKeys();

        // 공개 키 목록이 저장되어 있다면 키 목록 가져오고 파싱 진행
        String keyString = redisRepository.findPublicKey(PROVIDER).get().getKey();
        return parseKeys(keyString);
    }

    private void setPublicKeys() throws IOException {
        // 공개키 목록 조회 URL 요청
        URL url = new URL(REQUEST_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode <= 300) {
            String jsonData = getJsonData(conn);
            redisRepository.save(PublicKeyDto.builder()
                    .provider("KAKAO")
                    .key(jsonData)
                    .build());
        } else {
            System.out.println("RESPONSE_CODE = " + responseCode);
            throw new IOException();
        }
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

    private String getKidFromToken(String idToken) {
        // ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String header = getHeader(idToken);
        // 헤더를 Base64 방식으로 디코딩
        String decodedHeader = new String(Decoders.BASE64.decode(header));
        // JsonParser 사용하여 헤더에 있는 kid 값 불러오기
        JsonElement element = JsonParser.parseString(decodedHeader.toString());
        return element.getAsJsonObject().get("kid").getAsString();
    }

    private String getHeader(String idToken) {
        String[] dividedToken = splitToken(idToken);
        return dividedToken[0];
    }

    private String[] splitToken(String idToken) {
        String[] dividedToken = idToken.split("\\.");
        if (dividedToken.length != 3)
            throw new JwtHandler(ErrorStatus.JWT_TOKEN_INVALID);
        return dividedToken;
    }

    private String getJsonData(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        String jsonData = "";
        List<String> keys = new ArrayList<>();

        while ((line = br.readLine()) != null)
            jsonData += line;
        System.out.println("jsonData = " + jsonData);
        return jsonData;
    }

    private Key getRSAPublicKey(String kty, String modulus, String exponent) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance(kty);
        byte[] decodeM = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
        BigInteger m = new BigInteger(1, decodeM);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(m, e);
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }
}

