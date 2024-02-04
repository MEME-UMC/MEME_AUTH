package umc.meme.auth.global.auth;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.global.auth.dto.AuthRequest;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인", description = "소셜 로그인할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @Operation(summary = "토큰 재발급", description = "토큰이 만료가 되었을때 재발급을 받는 API입니다")
    @PostMapping("/api/v0/auth/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthRequest.ReissueDto reissueDto) {
        return ResponseEntity.ok(authService.reissue(reissueDto));
    }

    @Operation(summary = "로그아웃", description = "로그아웃하는 API입니다")
    @PostMapping("/api/v0/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") AuthRequest.AccessTokenDto requestAccessTokenDto) {
        authService.logout(requestAccessTokenDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

//    @PostMapping("/api/v0/auth/withdraw")
//    public ResponseEntity<?> withdraw(@RequestHeader("Authorization") AuthRequest.AccessTokenDto requestAccessTokenDto) {
//        authService
//    }
}
