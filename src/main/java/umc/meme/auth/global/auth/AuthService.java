package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.domain.token.domain.RefreshToken;
import umc.meme.auth.domain.token.domain.RefreshTokenRepository;
import umc.meme.auth.domain.token.dto.RefreshRequest;
import umc.meme.auth.global.jwt.JwtTokenProvider;
import umc.meme.auth.global.oauth.oAuthService;

import java.util.HashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PrincipalDetailsService principalDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final oAuthService oAuthService;

    private final static String TOKEN_PREFIX = "Bearer ";

    @Transactional
    public AuthResponse.TokenDto login(AuthRequest.LoginDto loginDto) {
        Authentication authentication;
        try {
            //User userInfo = oAuthService.getUserInfo(loginDto.getAccessToken());
            //System.out.println("userInfo = " + userInfo.getUsername() + " " + userInfo.getEmail());
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getEmail()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException exception) {
            throw new DisabledException("DISABLED_EXCEPTION", exception);
        } catch (LockedException exception) {
            throw new LockedException("LOCKED_EXCEPTION", exception);
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("BAD_CREDENTIALS_EXCEPTION", exception);
        }

        UserDetails userDetails = principalDetailsService.loadUserByUsername(loginDto.getUsername());
        AuthResponse.TokenDto tokenDto = generateToken(userDetails.getUsername(), getAuthorities(authentication));
        return tokenDto;
    }

    @Transactional
    public AuthResponse.TokenDto reissue(RefreshRequest.TokenDto tokenDto) {
        String requestRefreshToken = tokenDto.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findById(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("No Refresh Token"));

        if (jwtTokenProvider.isTokenExpired(refreshToken.getAccessToken())) {
            System.out.println("Access Token Expired");
            Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken.getAccessToken());
            UserDetails userDetails = principalDetailsService.loadUserByUsername(authentication.getName());
            return generateToken(userDetails.getUsername(), getAuthorities(authentication));
        } else {
            System.out.println("Access Token Non-Expired");
            refreshTokenRepository.delete(refreshToken);
        }

        return new AuthResponse.TokenDto(null, null);
    }

    private AuthResponse.TokenDto generateToken(String username, String authorities) {
        AuthResponse.TokenDto tokenDto = jwtTokenProvider.createToken(username, authorities);
        saveRefreshToken(tokenDto.getRefreshToken(), tokenDto.getAccessToken());
        return tokenDto;
    }

    private void saveRefreshToken(String refreshToken, String accessToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build());
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
