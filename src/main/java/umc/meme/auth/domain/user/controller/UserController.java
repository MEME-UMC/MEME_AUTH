package umc.meme.auth.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.domain.user.dto.UserResponse;
import umc.meme.auth.domain.user.service.UserService;
import umc.meme.auth.global.common.BaseResponseDto;
import umc.meme.auth.global.common.status.SuccessStatus;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "모델 회원가입", description = "모델이 회원가입할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/model/signup")
    public BaseResponseDto<UserResponse.JoinSuccessDto> modelSignUp(@RequestBody UserRequest.ModelJoinDto joinDto) {
        return BaseResponseDto.SuccessResponse(SuccessStatus.MODEL_JOIN_SUCCESS, userService.modelSignUp(joinDto));
    }

    @Operation(summary = "아티스트 회원가입", description = "아티스트가 회원가입할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/artist/signup")
    public BaseResponseDto<UserResponse.JoinSuccessDto> artistSignUp(@RequestBody UserRequest.ArtistJoinDto joinDto) {
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_JOIN_SUCCESS, userService.artistSignUp(joinDto));
    }

    @Operation(summary = "아티스트 추가 회원가입", description = "아티스트가 회원가입하고 추가 정보를 입력할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/artist/extra")
    public BaseResponseDto<?> artistExtra(@RequestBody UserRequest.ArtistExtraDto joinDto) {
        userService.artistExtra(joinDto);
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_EXTRA_JOIN_SUCCESS);
    }
}
