package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.artist.entity.Artist;
import umc.meme.auth.domain.artist.entity.ArtistRepository;
import umc.meme.auth.domain.model.entity.Model;
import umc.meme.auth.domain.model.entity.ModelRepository;
import umc.meme.auth.domain.token.entity.Token;
import umc.meme.auth.domain.token.entity.TokenRepository;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.domain.user.entity.UserRepository;
import umc.meme.auth.global.auth.converter.TokenConverter;
import umc.meme.auth.global.auth.converter.UserConverter;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.exception.AuthException;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.jwt.JwtTokenProvider;
import umc.meme.auth.global.oauth.provider.OAuthProvider;
import umc.meme.auth.global.oauth.provider.apple.AppleAuthProvider;
import umc.meme.auth.global.oauth.provider.kakao.KakaoAuthProvider;

import java.util.Optional;

import static umc.meme.auth.global.common.status.ErrorStatus.*;
import static umc.meme.auth.global.enums.Provider.APPLE;
import static umc.meme.auth.global.enums.Provider.KAKAO;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final ModelRepository modelRepository;  // 필수 - 사용자 저장
    private final ArtistRepository artistRepository;  // 필수 - 사용자 저장
    private final AuthenticationManager authenticationManager;  // 필수 - 로그인
    private final JwtTokenProvider jwtTokenProvider;  // 필수 - 토큰 생성
    private final TokenRepository tokenRepository;  // 필수 - 토큰 저장 및 삭제
    private final UserRepository userRepository;  // 필수 - 사용자 정보 조회
    private final RedisRepository redisRepository;  // 필수 - 자식 클래스 의존성 주입 시 필요

    private final static String TOKEN_PREFIX = "Bearer ";
    private static final String USERNAME = "username";
    private final static String ROLE_MODEL = "MODEL";
    private final static String ROLE_ARTIST = "ARTIST";

    @Transactional
    public AuthResponse.JoinDto signupModel(AuthRequest.ModelJoinDto modelJoinDto) {
        // ID 토큰을 뜯는 메서드를 따로 만들어야하나..
        String userEmail = getUserEmail(modelJoinDto.getId_token(), modelJoinDto.getProvider());
        User user = saveUser(modelJoinDto, userEmail);
        String[] tokenPair = login(user);
        return TokenConverter.toJoinDto(user, tokenPair, ROLE_MODEL);
    }

    @Transactional
    public AuthResponse.JoinDto signupArtist(AuthRequest.ArtistJoinDto artistJoinDto) {
        String userEmail = getUserEmail(artistJoinDto.getId_token(), artistJoinDto.getProvider());
        User user = saveUser(artistJoinDto, userEmail);
        String[] tokenPair = login(user);
        return TokenConverter.toJoinDto(user, tokenPair, ROLE_ARTIST);
    }

    @Transactional
    public AuthResponse.TokenDto reissue(AuthRequest.ReissueDto reissueDto) throws AuthException {
        String requestAccessToken = reissueDto.getAccess_token();
        String requestRefreshToken = reissueDto.getRefresh_token();

        Token requestToken = tokenRepository.findByAccessToken(requestAccessToken)
                .orElseThrow(() -> new AuthException(CANNOT_FOUND_USER));

        if (requestToken.getRefreshToken() == null) {
            // Case 1 : refresh token을 가지고 있지 않은 경우
            deleteTokenPairInRedis(requestAccessToken);
            throw new AuthException(NO_REFRESH_TOKEN);
        } else if (!requestToken.getRefreshToken().equals(requestRefreshToken)) {
            // Case 2 : access token과 refresh token이 일치하지 않는 경우 -> 토큰 탈취 가능성 존재
            deleteTokenPairInRedis(requestAccessToken);
            throw new AuthException(ANOTHER_USER);
        } else {
            // Case 3 : 정상적인 경우
            deleteTokenPairInRedis(requestAccessToken);
            Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
            String[] tokenPair = jwtTokenProvider.createTokenPair(authentication);

            return TokenConverter.toTokenDto(tokenPair);
        }
    }

    @Transactional
    public void logout(String requestHeader) throws AuthException {
        String requestAccessToken = resolveToken(requestHeader);
        deleteTokenPairInRedis(requestAccessToken);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void withdraw(String requestHeader) throws AuthException {
        String requestAccessToken = resolveToken(requestHeader);
        String username = (String) jwtTokenProvider.getClaims(requestAccessToken).get(USERNAME);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Transactional
    public AuthResponse.UserInfoDto checkUserExistsFindByEmail(AuthRequest.IdTokenDto idTokenDto) {
        String userEmail = getUserEmail(idTokenDto.getId_token(), idTokenDto.getProvider());
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty())
            return UserConverter.toUserInfoDtoNonExists();

        User user = userOptional.get();
        String[] tokenPair = login(user);

        return UserConverter.toUserInfoDtoExists(user, tokenPair);
    }

    @Transactional
    public boolean checkNicknameDuplicate(AuthRequest.NicknameDto nicknameDto) {
        return userRepository.existsByNickname(nicknameDto.getNickname());
    }

    private Model saveUser(AuthRequest.ModelJoinDto modelJoinDto, String userEmail) {
        return modelRepository.save(UserConverter.toModel(modelJoinDto, userEmail, ROLE_MODEL));
    }

    private Artist saveUser(AuthRequest.ArtistJoinDto artistJoinDto, String userEmail) {
        return artistRepository.save(UserConverter.toArtist(artistJoinDto, userEmail, ROLE_ARTIST));
    }

    private String[] login(User user) {
        Authentication authentication = authenticate(user);

        String[] tokenPair = jwtTokenProvider.createTokenPair(authentication);
        String accessToken = tokenPair[0];
        String refreshToken = tokenPair[1];
        saveTokenPairInRedis(accessToken, refreshToken);

        return tokenPair;
    }

    private Authentication authenticate(User user) throws AuthException {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getEmail()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException exception) {
            throw new DisabledException("DISABLED_EXCEPTION", exception);
        } catch (LockedException exception) {
            throw new LockedException("LOCKED_EXCEPTION", exception);
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("BAD_CREDENTIALS_EXCEPTION", exception);
        }

        return authentication;
    }

    private String getUserEmail(String idToken, Provider provider) throws AuthException {
        OAuthProvider oAuthProvider;

        if (provider.equals(KAKAO)) {  // Use Kakao OpenID Connect
            oAuthProvider = new KakaoAuthProvider(redisRepository);
        } else if (provider.equals(APPLE)) {  // Use Apple OpenID Connect
            oAuthProvider = new AppleAuthProvider(redisRepository);
        } else {
            throw new AuthException(PROVIDER_ERROR);
        }

        return oAuthProvider.getUserEmail(idToken);
    }

    private void saveTokenPairInRedis(String accessToken, String refreshToken) {
        tokenRepository.save(Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    private void deleteTokenPairInRedis(String requestAccessToken) throws AuthException {
        Token findToken = tokenRepository.findByAccessToken(requestAccessToken)
                .orElseThrow(() -> new AuthException(TOKEN_MISMATCH_EXCEPTION));
        tokenRepository.delete(findToken);
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // TODO : Need Refactoring
    // 현재 API 중단 상태 -> 추후 리팩토링 예정
//    @Transactional
//    public void signupArtistExtra(AuthRequest.ArtistExtraDto artistExtraDto) {
//        Artist artist = artistRepository.findById(artistExtraDto.getUser_id())
//                .orElseThrow(() -> new AuthException(ARTIST_NOT_FOUND));
//        artist.update(artistExtraDto);
//    }
}
