package umc.meme.auth.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.common.BaseResponseDto;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.common.status.SuccessStatus;
import umc.meme.auth.global.exception.handler.AuthException;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v1/login")
    public BaseResponseDto<AuthResponse.TokenDto> login(@RequestBody AuthRequest.LoginDto loginDto) throws AuthException {
        return BaseResponseDto.SuccessResponse(SuccessStatus.LOGIN_SUCCESS, authService.login(loginDto));
    }

    @PostMapping("/api/v1/reissue")
    public BaseResponseDto<?> reissue(@RequestBody AuthRequest.ReissueDto reissueDto) {
        AuthResponse.TokenDto reissueResult = authService.reissue(reissueDto);

        if (reissueResult.getAccessToken() == null)
            return BaseResponseDto.ErrorResponse(ErrorStatus.CANNOT_REISSUE_JWT_TOKEN);

        return BaseResponseDto.SuccessResponse(SuccessStatus.REISSUE_SUCCESS, reissueResult);
    }

    @PostMapping("/api/v1/auth/logout")
    public BaseResponseDto<?> logout(HttpServletRequest request) {
        System.out.println(request);
        System.out.println(request.getHeader("Authorization"));
        String header = request.getHeader("Authorization");
        authService.logout(header);
        return BaseResponseDto.SuccessResponse(SuccessStatus.LOGOUT_SUCCESS);
    }

    @PostMapping("/api/v1/auth/withdraw")
    public BaseResponseDto<?> withdraw(@RequestHeader("Authorization") AuthRequest.AccessTokenDto requestAccessTokenDto) {
        authService.withdraw(requestAccessTokenDto);
        return BaseResponseDto.SuccessResponse(SuccessStatus.WITHDRAW_SUCCESS);
    }
}
