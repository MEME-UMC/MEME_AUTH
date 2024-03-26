package umc.meme.auth.global.exception.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import umc.meme.auth.global.common.BaseResponseDto;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.AuthException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = AuthException.class)
    public BaseResponseDto<?> invokeError(AuthException e) {
        return BaseResponseDto.ErrorResponse((ErrorStatus) e.getBaseErrorCode());
    }
}
