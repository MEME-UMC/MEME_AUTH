package umc.meme.auth.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.global.auth.PrincipalDetailsService;
import umc.meme.auth.global.auth.dto.AuthResponse;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final PrincipalDetailsService principalDetailsService;

    private static final String TOKEN_KEY = "username";

    @Transactional
    public AuthResponse.TokenDto createToken(String username, String authorities) {
        Long now = System.currentTimeMillis();
        String accessToken = createAccessToken(username, authorities, now);
        String refreshToken = createRefreshToken(now);
        return new AuthResponse.TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse.AccessTokenDto reissueAccessToken(String username, String authorities) {
        Long now = System.currentTimeMillis();
        String accessToken = createAccessToken(username, authorities, now);
        return new AuthResponse.AccessTokenDto(accessToken);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getKey())
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new JwtException("Validate Access Token Exception");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Validate Access Token Exception");  // ExpiredJwtException 던지고 싶은데 고민
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("IllegalArgumentException");
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String accessToken) {
        String username = getClaims(accessToken).get(TOKEN_KEY).toString();
        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String createAccessToken(String username, String authorities, Long now) {
        return Jwts.builder()
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("typ", "JWT")
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + (jwtProperties.getAccessTokenValidityInSeconds() * 1000)))
                .setSubject("access-token")
                .claim("username", username)
                .claim("role", authorities)
                .signWith(jwtProperties.getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private String createRefreshToken(Long now) {
        return Jwts.builder()
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("typ", "JWT")
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + (jwtProperties.getRefreshTokenValidityInSeconds() * 1000)))
                .setSubject("refresh-token")
                .signWith(jwtProperties.getKey(), SignatureAlgorithm.HS512)
                .compact();
    }
}
