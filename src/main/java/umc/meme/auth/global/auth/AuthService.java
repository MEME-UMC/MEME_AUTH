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
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.config.SecurityConfig;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.enums.UserStatus;
import umc.meme.auth.global.exception.GeneralException;
import umc.meme.auth.global.exception.handler.AuthException;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.jwt.JwtTokenProvider;
import umc.meme.auth.global.oauth.OAuthService;
import umc.meme.auth.global.oauth.apple.AppleAuthService;
import umc.meme.auth.global.oauth.kakao.KakaoAuthService;

import java.time.LocalDate;
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

    private final static String TOKEN_PREFIX = "Bearer ";

    @Transactional
    public AuthResponse.TokenDto signupModel(AuthRequest.ModelJoinDto modelJoinDto) {
        String userEmail = getUser(modelJoinDto.getId_token(), modelJoinDto.getProvider());
        String nickName = modelJoinDto.getNickname();

        if(modelRepository.existsByNickName(nickName)
                || artistRepository.existsByNickName(nickName))
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATED);

        User user = Model.builder()
                .email(userEmail)
                .provider(modelJoinDto.getProvider())
                .profileImg(modelJoinDto.getProfileImg())
                .username(modelJoinDto.getUsername())
                .nickname(nickName)
                .gender(modelJoinDto.getGender())
                .skinType(modelJoinDto.getSkinType())
                .personalColor(modelJoinDto.getPersonalColor())
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .role("MODEL")
                .inactiveDate(LocalDate.of(2099,12,31))
                .userStatus(UserStatus.ACTIVE)
                .build();

        Long userId = modelRepository.save((Model) user).getUserId();

        AuthResponse.TokenDto tokenDto = login(user);
        tokenDto.setUserId(userId);
        tokenDto.setDetails(true);

        return tokenDto;
    }

    @Transactional
    public AuthResponse.TokenDto signupArtist(AuthRequest.ArtistJoinDto artistJoinDto) {
        String userEmail = getUser(artistJoinDto.getId_token(), artistJoinDto.getProvider());
        String nickName = artistJoinDto.getNickname();

        if(modelRepository.existsByNickName(nickName)
                || artistRepository.existsByNickName(nickName))
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATED);

        User user = Artist.builder()
                .email(userEmail)
                .provider(artistJoinDto.getProvider())
                .profileImg(artistJoinDto.getProfileImg())
                .username(artistJoinDto.getUsername())
                .nickname(nickName)
                .password(SecurityConfig.passwordEncoder().encode(userEmail))
                .role("ARTIST")
                .details(false)
                .inactiveDate(LocalDate.of(2099,12,31))
                .userStatus(UserStatus.ACTIVE)
                .build();

        Long userId = artistRepository.save((Artist) user).getUserId();

        AuthResponse.TokenDto tokenDto = login(user);
        tokenDto.setUserId(userId);
        tokenDto.setDetails(user.getDetails());

        return tokenDto;
    }

    @Transactional
    public void signupArtistExtra(AuthRequest.ArtistExtraDto artistExtraDto) {
        Artist artist = artistRepository.findById(artistExtraDto.getUserId())
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
        } catch (AuthException exception) {
            throw exception;
        }

        UserDetails userDetails = principalDetailsService.loadUserByUsername(user.getUsername());
        AuthResponse.TokenDto tokenDto = generateToken(userDetails.getUsername(), getAuthorities(authentication));
        return tokenDto;
    }

    private String getUser(String idToken, Provider provider) throws AuthException {
        OAuthService oAuthService;

        if (provider.equals(KAKAO)) {
            oAuthService = new KakaoAuthService(userRepository, redisRepository);
        } else if (provider.equals(APPLE)) {
            oAuthService = new AppleAuthService(userRepository, redisRepository);
        } else {
            throw new AuthException(PROVIDER_ERROR);
        }

        return oAuthService.getUserInfo(idToken);
    }

    @Transactional
    public AuthResponse.TokenDto reissue(AuthRequest.ReissueDto reissueDto) throws AuthException {
        String requestAccessToken = reissueDto.getAccessToken();
        String requestRefreshToken = reissueDto.getRefreshToken();

        Token requestToken = tokenRepository.findByAccessToken(requestAccessToken)
                .orElseThrow(() -> new AuthException(CANNOT_FOUND_USER));

        if (requestToken.getRefreshToken() == null) {
            deleteRefreshToken(requestAccessToken);
            throw new AuthException(NO_REFRESH_TOKEN);
        }

        if (!requestToken.getRefreshToken().equals(requestRefreshToken)) {
            deleteRefreshToken(requestAccessToken);
            throw new AuthException(ANOTHER_USER);
        }

        deleteRefreshToken(requestAccessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        UserDetails userDetails = principalDetailsService.loadUserByUsername(authentication.getName());
        return generateToken(userDetails.getUsername(), getAuthorities(authentication));
    }

    @Transactional
    public void logout(String requestHeader) throws AuthException {
        String requestAccessToken = resolveToken(requestHeader);
        deleteRefreshToken(requestAccessToken);
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

    private AuthResponse.TokenDto generateToken(String username, String authorities) {
        AuthResponse.TokenDto tokenDto = jwtTokenProvider.createToken(username, authorities);
        saveRefreshToken(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        return tokenDto;
    }

    private void saveRefreshToken(String accessToken, String refreshToken) {
        tokenRepository.save(Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    private void deleteRefreshToken(String accessToken) throws AuthException {
        Token findToken = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new AuthException(TOKEN_MISMATCH_EXCEPTION));
        tokenRepository.delete(findToken);
    }

    public String getAuthorities(Authentication authentication) {
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
