package umc.meme.auth.domain.token.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.domain.token.dto.AccessTokenRequest;
import umc.meme.auth.domain.token.dto.TokenDto;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.global.jwt.JwtFilter;
import umc.meme.auth.global.jwt.TokenProvider;

/**
 * @author : sblim
 * @version : 1.0.0
 * @package : com.karim.jwt.controller
 * @name : spring-basic-server
 * @date : 2023. 04. 27. 027 오후 2:39
 * @modifyed :
 * @description :
 **/

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody UserRequest.LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getEmail());
        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        TokenDto jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        // response header에 jwt token에 넣어줌
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt.getAccessToken());

        // tokenDto를 이용해 response body에도 넣어서 리턴
        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<TokenDto> generateAccessToken(@RequestBody final AccessTokenRequest request) throws Exception {
        TokenDto token = tokenProvider.generateAccessToken(request);
        return ResponseEntity.ok(token);
    }
}