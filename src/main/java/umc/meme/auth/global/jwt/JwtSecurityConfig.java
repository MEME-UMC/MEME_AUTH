package umc.meme.auth.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author : sblim
 * @version : 1.0.0
 * @package : com.karim.jwt.jwt
 * @name : spring-basic-server
 * @date : 2023. 04. 27. 027 오전 11:30
 * @modifyed :
 * @description :
 **/

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;

    @Override
    public void configure(HttpSecurity http) {
        JwtExceptionFilter exceptionFilter = new JwtExceptionFilter();

        // security 로직에 JwtFilter 등록
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
        http.addFilterBefore(exceptionFilter, JwtFilter.class);

    }
}