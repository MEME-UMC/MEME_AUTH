package umc.meme.auth.global.auth;

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

    @PostMapping("/api/v0/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping("/api/v0/auth/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthRequest.ReissueDto reissueDto) {
        return ResponseEntity.ok(authService.reissue(reissueDto));
    }

    @PostMapping("/api/v0/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") AuthRequest.AccessTokenDto requestAccessTokenDto) {
        authService.logout(requestAccessTokenDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/v0/auth/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader("Authorization") AuthRequest.AccessTokenDto requestAccessTokenDto) {
        authService.withdraw(requestAccessTokenDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
