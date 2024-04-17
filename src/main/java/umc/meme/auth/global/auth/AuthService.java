package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.config.SecurityConfig;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.exception.AuthException;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.jwt.JwtTokenProvider;
import umc.meme.auth.global.oauth.provider.OAuthProvider;
import umc.meme.auth.global.oauth.provider.apple.AppleAuthProvider;
import umc.meme.auth.global.oauth.provider.kakao.KakaoAuthProvider;

import java.util.Optional;
import java.util.stream.Collectors;

import static umc.meme.auth.global.common.status.ErrorStatus.*;
import static umc.meme.auth.global.enums.Provider.APPLE;
import static umc.meme.auth.global.enums.Provider.KAKAO;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PrincipalDetailsService principalDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final ModelRepository modelRepository;
    private final ArtistRepository artistRepository;
    private final KakaoAuthProvider kakaoAuthProvider;
    private final AppleAuthProvider appleAuthProvider;

    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String ROLE_MODEL = "MODEL";
    private final static String ROLE_ARTIST = "ARTIST";

    @Transactional
    public AuthResponse.TokenDto signupModel(AuthRequest.ModelJoinDto modelJoinDto) {
        String userEmail = getUserEmail(modelJoinDto.getId_token(), modelJoinDto.getProvider());

        Model user = Model.builder()
                .role(ROLE_MODEL)
                .email(userEmail)
                .provider(modelJoinDto.getProvider())
                .profileImg(modelJoinDto.getProfile_img())
                .username(modelJoinDto.getUsername())
                .nickname(modelJoinDto.getNickname())
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .details(true)
                .gender(modelJoinDto.getGender())
                .skinType(modelJoinDto.getSkin_type())
                .personalColor(modelJoinDto.getPersonal_color())
                .build();

        Long userId = modelRepository.save(user).getUserId();

        AuthResponse.TokenDto tokenDto = login(user);
        tokenDto.setUser_id(userId);
        tokenDto.setDetails(user.getDetails());
        tokenDto.setType(ROLE_MODEL);

        return tokenDto;
    }

    @Transactional
    public AuthResponse.TokenDto signupArtist(AuthRequest.ArtistJoinDto artistJoinDto) {
        String userEmail = getUserEmail(artistJoinDto.getId_token(), artistJoinDto.getProvider());

        Artist user = Artist.builder()
                .role(ROLE_ARTIST)
                .email(userEmail)
                .provider(artistJoinDto.getProvider())
                .profileImg(artistJoinDto.getProfile_img())
                .username(artistJoinDto.getUsername())
                .nickname(artistJoinDto.getNickname())
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .details(false)
                .build();

        Long userId = artistRepository.save(user).getUserId();

        AuthResponse.TokenDto tokenDto = login(user);
        tokenDto.setUser_id(userId);
        tokenDto.setDetails(user.getDetails());
        tokenDto.setType(ROLE_ARTIST);

        return tokenDto;
    }

    @Transactional
    public void signupArtistExtra(AuthRequest.ArtistExtraDto artistExtraDto) {
        Artist artist = artistRepository.findById(artistExtraDto.getUser_id())
                .orElseThrow(() -> new AuthException(ARTIST_NOT_FOUND));
        artist.update(artistExtraDto);
    }

    @Transactional
    public AuthResponse.TokenDto login(User user) throws AuthException {
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

        UserDetails userDetails = principalDetailsService.loadUserByUsername(user.getUsername());
        return generateToken(userDetails.getUsername(), getAuthorities(authentication));
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
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        // login 부분이랑 비슷하네
        UserDetails userDetails = principalDetailsService.loadUserByUsername(authentication.getName());
        return generateToken(userDetails.getUsername(), getAuthorities(authentication));
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
        String username = (String) jwtTokenProvider.getClaims(requestAccessToken).get("username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND));
        userRepository.delete(user);
    }

    // 회원 등록 여부 조회
    @Transactional
    public AuthResponse.UserInfoDto checkUserExistsFindByEmail(AuthRequest.IdTokenDto idTokenDto) {
        String email = "";

        if (idTokenDto.getProvider() == KAKAO) {
            email = kakaoAuthProvider.getUserInfo(idTokenDto.getId_token());
        } else if (idTokenDto.getProvider() == APPLE) {
            email = appleAuthProvider.getUserInfo(idTokenDto.getId_token());
        }

        // ID 토큰을 파라미터로 받음
        Optional<User> userOptional = userRepository.findByEmail(email);

        AuthResponse.UserInfoDto userInfoDto = new AuthResponse.UserInfoDto();

        if (userOptional.isPresent()) {
            AuthResponse.TokenDto loginDto = login(userOptional.get());

            userInfoDto.setUser(true);
            userInfoDto.setUser_id(userOptional.get().getUserId());
            userInfoDto.setRole(userOptional.get().getRole());
            userInfoDto.setAccess_token(loginDto.getAccess_token());
            userInfoDto.setRefresh_token(loginDto.getRefresh_token());
        } else {
            userInfoDto.setUser(false);
        }

        return userInfoDto;
    }

    @Transactional
    public boolean checkNicknameDuplicate(AuthRequest.NicknameDto nicknameDto) {
        return userRepository.existsByNickname(nicknameDto.getNickname());
    }

    private String getUserEmail(String idToken, Provider provider) throws AuthException {
        OAuthProvider oAuthProvider;

        if (provider.equals(KAKAO)) {
            oAuthProvider = new KakaoAuthProvider(redisRepository);
        } else if (provider.equals(APPLE)) {
            oAuthProvider = new AppleAuthProvider(redisRepository);
        } else {
            throw new AuthException(PROVIDER_ERROR);
        }

        return oAuthProvider.getUserInfo(idToken);
    }

    private AuthResponse.TokenDto generateToken(String username, String authorities) {
        AuthResponse.TokenDto tokenDto = jwtTokenProvider.createToken(username, authorities);
        saveTokenPairInRedis(tokenDto.getAccess_token(), tokenDto.getRefresh_token());
        return tokenDto;
    }

    private void saveTokenPairInRedis(String accessToken, String refreshToken) {
        tokenRepository.save(Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    private void deleteTokenPairInRedis(String accessToken) throws AuthException {
        Token findToken = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new AuthException(TOKEN_MISMATCH_EXCEPTION));
        tokenRepository.delete(findToken);
    }

    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
