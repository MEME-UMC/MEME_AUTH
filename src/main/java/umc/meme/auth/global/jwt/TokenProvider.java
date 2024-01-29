package umc.meme.auth.global.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import umc.meme.auth.domain.token.domain.RefreshToken;
import umc.meme.auth.domain.token.dto.AccessTokenRequest;
import umc.meme.auth.domain.token.dto.TokenDto;
import umc.meme.auth.domain.token.repository.RefreshTokenRepository;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.JwtHandler;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author : sblim
 * @version : 1.0.0
 * @package : com.karim.jwt.jwt
 * @name : spring-basic-server
 * @date : 2023. 04. 27. 027 오전 11:20
 * @modifyed :
 * @description :
 **/

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    private static final long ACCESS_TOKEN_EXPIRES = 1 * 1000L;

    private static final long REFRESH_TOKEN_EXPIRES = 7 * 24 * 60 * 60 * 1000L;

    private final UserRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            final UserRepository memberRepository,
            final RefreshTokenRepository refreshTokenRepository) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당하기 위해
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        umc.meme.auth.domain.user.domain.User user = memberRepository.findByUsername(authentication.getName()).orElseThrow();


        // 토큰의 expire 시간을 설정
        Date now = new Date();
        Date access_expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRES);
        Date refresh_expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRES);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put(AUTHORITIES_KEY, authorities);

        // access_token 생성
        String accessToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .setSubject(authentication.getName())
                .setExpiration(access_expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // refresh_token 생성
        String refreshToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(refresh_expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        RefreshToken refreshToken1 = new RefreshToken(refreshToken, user.getUserid());
        refreshTokenRepository.save(refreshToken1);

        // access_token과 refresh_token을 조합하여 반환
        return new TokenDto(accessToken, refreshToken1.getRefreshToken());
    }


    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰의 유효성 검증을 수행
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다");
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);
        }catch (ExpiredJwtException e){
            logger.info("만료된 JWT 토큰입니다");
            throw new JwtHandler(ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED);
        }catch (UnsupportedJwtException e){
            logger.info("지원되지 않는 JWT 토큰입니다");
            throw new JwtHandler(ErrorStatus.JWT_TOKEN_UNSUPPORTED);
        }catch (IllegalArgumentException e){
            logger.info("JWT 토큰이 잘못되었습니다");
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);
        }
    }

    public TokenDto generateAccessToken(final AccessTokenRequest request) throws Exception {
        RefreshToken refreshToken = refreshTokenRepository.findById(String.valueOf(request.getRefreshToken()))
                .orElseThrow(() -> new JwtHandler(ErrorStatus.JWT_TOKEN_NOT_FOUND));
        Long memberId = refreshToken.getMemberId();

        System.out.println("refreshToken = " + refreshToken.getRefreshToken());

        refreshTokenRepository.delete(refreshToken);

        Date now = new Date();
        Date access_expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRES);
        Date refresh_expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRES);

        String accessToken = Jwts.builder()
                .signWith(key)
                .setIssuedAt(now)
                .setExpiration(access_expiration)
                .setSubject(String.valueOf(refreshToken.getMemberId()))
                .compact();

        String newRefreshToken = Jwts.builder()
                .signWith(key)
                .setIssuedAt(now)
                .setExpiration(refresh_expiration)
                .setSubject(String.valueOf(refreshToken.getMemberId()))
                .compact();

        refreshTokenRepository.save(new RefreshToken(newRefreshToken, memberId));

        return new TokenDto(accessToken, newRefreshToken);
    }

    public Long extractMemberId(final String accessToken) {
        try {
            String memberId = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
            return Long.parseLong(memberId);
        } catch (final JwtException e) {
            throw new RuntimeException();
        }
    }

    //    public String createToken(Authentication authentication) {
//
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        // 토큰의 expire 시간을 설정
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + this.tokenValidityInMilliseconds);
//
//        return Jwts.builder()
//                .setSubject(authentication.getName())
//                .claim(AUTHORITIES_KEY, authorities)
//                .signWith(key, SignatureAlgorithm.HS512)
////                .setExpiration(validity) // 토큰 무제한 사용
//                .compact();
//    }

    //    public RefreshTokenResponse generateRefreshToken(final RefreshTokenRequest request) throws Exception {
//        Long memberId = memberRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND))
//                .getUserId();
//
//
//        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(), memberId);
//        refreshTokenRepository.save(refreshToken);
//
//        return new RefreshTokenResponse(refreshToken);
//    }
}

