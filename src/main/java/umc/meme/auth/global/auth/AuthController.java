package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.domain.token.dto.RefreshRequest;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v0/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping("/api/v0/auth/reissue")
    public ResponseEntity<?> reissue(@RequestBody RefreshRequest.TokenDto tokenDto) {
        return ResponseEntity.ok(authService.reissue(tokenDto));
    }
}
