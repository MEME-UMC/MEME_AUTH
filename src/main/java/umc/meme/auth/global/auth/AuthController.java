package umc.meme.auth.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.common.BaseResponseDto;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.common.status.SuccessStatus;
import umc.meme.auth.global.exception.AuthException;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v1/signup/model")
    public BaseResponseDto<AuthResponse.TokenDto> signupModel(@RequestBody AuthRequest.ModelJoinDto modelJoinDto) throws AuthException{
        return BaseResponseDto.SuccessResponse(SuccessStatus.MODEL_JOIN_SUCCESS, authService.signupModel(modelJoinDto));
    }

    @PostMapping("/api/v1/signup/artist")
    public BaseResponseDto<AuthResponse.TokenDto> signupArtist(@RequestBody AuthRequest.ArtistJoinDto artistJoinDto) throws AuthException {
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_JOIN_SUCCESS, authService.signupArtist(artistJoinDto));
    }

    @PostMapping("/api/v1/auth/artist/extra")
    public BaseResponseDto<?> signupArtistExtra(@RequestBody AuthRequest.ArtistExtraDto artistExtraDto) throws AuthException {
        authService.signupArtistExtra(artistExtraDto);
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_EXTRA_JOIN_SUCCESS);
    }

    @PostMapping("/api/v1/reissue")
    public BaseResponseDto<?> reissue(@RequestBody AuthRequest.ReissueDto reissueDto) throws AuthException {
        AuthResponse.TokenDto reissueResult = authService.reissue(reissueDto);

        if (reissueResult.getAccessToken() == null)
            return BaseResponseDto.ErrorResponse(ErrorStatus.CANNOT_REISSUE_JWT_TOKEN);

        return BaseResponseDto.SuccessResponse(SuccessStatus.REISSUE_SUCCESS, reissueResult);
    }

    @PostMapping("/api/v1/auth/logout")
    public BaseResponseDto<?> logout(HttpServletRequest request) throws AuthException {
        authService.logout(request.getHeader("Authorization"));
        return BaseResponseDto.SuccessResponse(SuccessStatus.LOGOUT_SUCCESS);
    }

    @PostMapping("/api/v1/auth/withdraw")
    public BaseResponseDto<?> withdraw(HttpServletRequest request) throws AuthException {
        authService.withdraw(request.getHeader("Authorization"));
        return BaseResponseDto.SuccessResponse(SuccessStatus.WITHDRAW_SUCCESS);
    }

    @PostMapping("/api/v1/check")
    public BaseResponseDto<?> checkUserExists(@RequestBody AuthRequest.IdTokenDto idTokenDto) throws AuthException {
        AuthResponse.UserInfoDto userInfoDto = authService.isUserExistsFindByEmail(idTokenDto);
        if (userInfoDto.isUser())
            return BaseResponseDto.SuccessResponse(SuccessStatus.USER_EXISTS, userInfoDto);
        else
            return BaseResponseDto.SuccessResponse(SuccessStatus.USER_NOT_EXISTS, userInfoDto);
    }
}
