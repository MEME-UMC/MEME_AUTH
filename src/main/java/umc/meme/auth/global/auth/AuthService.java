package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.token.entity.Token;
import umc.meme.auth.domain.token.entity.TokenRepository;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.domain.user.entity.UserRepository;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.AuthException;
import umc.meme.auth.global.exception.handler.JwtHandler;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.jwt.JwtTokenProvider;
import umc.meme.auth.global.oauth.OAuthService;
import umc.meme.auth.global.oauth.apple.AppleAuthService;
import umc.meme.auth.global.oauth.kakao.KakaoAuthService;

import java.util.stream.Collectors;

import static umc.meme.auth.global.common.status.ErrorStatus.TOKEN_MISMATCH_EXCEPTION;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PrincipalDetailsService principalDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    private final static String TOKEN_PREFIX = "Bearer ";

    @Transactional
    public AuthResponse.TokenDto login(AuthRequest.LoginDto loginDto) throws AuthException {
        String userName;
        Authentication authentication;
        try {
            User userInfo = getUser(loginDto);
            userName = userInfo.getUsername();
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userInfo.getEmail()));
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

        UserDetails userDetails = principalDetailsService.loadUserByUsername(userName);
        AuthResponse.TokenDto tokenDto = generateToken(userDetails.getUsername(), getAuthorities(authentication));
        return tokenDto;
    }

    private User getUser(AuthRequest.LoginDto loginDto) throws AuthException {
        OAuthService oAuthService;

        if (loginDto.getProvider().equals("KAKAO")) {
            oAuthService = new KakaoAuthService(userRepository, redisRepository);
        } else if (loginDto.getProvider().equals("APPLE")) {
            oAuthService = new AppleAuthService(userRepository, redisRepository);
        } else {
            throw new AuthException(ErrorStatus.PROVIDER_ERROR);
        }

        return oAuthService.getUserInfo(loginDto.getId_token());
    }

    @Transactional
    public AuthResponse.TokenDto reissue(AuthRequest.ReissueDto reissueDto) {
        String requestAccessToken = reissueDto.getAccessToken();
        String requestRefreshToken = reissueDto.getRefreshToken();

        Token requestToken = tokenRepository.findByAccessToken(requestAccessToken)
                .orElseThrow(() -> new IllegalArgumentException("TOKEN_MISMATCH_EXCEPTION"));

        if (requestToken.getRefreshToken() == null) {
            deleteRefreshToken(requestAccessToken);
            return new AuthResponse.TokenDto(null, null);
        }

        if (!requestToken.getRefreshToken().equals(requestRefreshToken)) {
            deleteRefreshToken(requestAccessToken);
            return new AuthResponse.TokenDto(null, null);
        }

        deleteRefreshToken(requestAccessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        UserDetails userDetails = principalDetailsService.loadUserByUsername(authentication.getName());
        return generateToken(userDetails.getUsername(), getAuthorities(authentication));
    }

    @Transactional
    public void logout(String header) {
        String requestAccessToken = resolveToken(header);
        deleteRefreshToken(requestAccessToken);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void withdraw(AuthRequest.AccessTokenDto requestAccessTokenDto) {
        // logout(requestAccessTokenDto);
        String requestAccessToken = resolveToken(requestAccessTokenDto.getAccessToken());
        String username = (String) jwtTokenProvider.getClaims(requestAccessToken).get("username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));
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

    private void deleteRefreshToken(String accessToken) {
        Token findToken = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new JwtHandler(TOKEN_MISMATCH_EXCEPTION));
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
