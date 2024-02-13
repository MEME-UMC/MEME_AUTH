package umc.meme.auth.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import umc.meme.auth.global.exception.handler.AuthException;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<?> invokeError(AuthException e) {
        System.out.println("=== AuthException.class ===");
        System.out.println("HTTP_STATUS = " + e.getReason().getHttpStatus());
        System.out.println("CODE = " + e.getReason().getCode());
        System.out.println("REASON = " + e.getReason().getMessage());
        return ResponseEntity.badRequest().build();
    }
}
