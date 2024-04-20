package umc.meme.auth.global.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.global.auth.PrincipalDetailsService;

import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final PrincipalDetailsService principalDetailsService;

    private static final String USERNAME = "username";

    @Transactional
    public String[] createTokenPair(Authentication authentication) {
        UserDetails userDetails = principalDetailsService.loadUserByUsername(authentication.getName());
        String username = userDetails.getUsername();
        String authorities = getAuthorities(authentication);

        Long now = System.currentTimeMillis();
        String accessToken = createAccessToken(username, authorities, now);
        String refreshToken = createRefreshToken(now);
        return new String[]{accessToken, refreshToken};  // index 0 : access_token, index 1 : refresh_token
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getKey())
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException e) {
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
        String username = getClaims(accessToken).get(USERNAME).toString();
        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
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
