package umc.meme.auth.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.meme.auth.global.common.ErrorReasonDto;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.JwtHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException{
        try{
            filterChain.doFilter(request,response);
        }catch (JwtHandler handler){
            response.setContentType("application/json; charset=UTF-8");

            String errorName = handler.getMessage();
            ErrorStatus errorStatus = ErrorStatus.valueOf(errorName);

            ErrorReasonDto errorReasonDto = ErrorReasonDto.builder()
                    .httpStatus(errorStatus.getHttpStatus())
                    .code(errorStatus.getCode())
                    .message(errorStatus.getMessage())
                    .success(false)
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(errorReasonDto);
            response.getWriter().write(json);
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

}
