package umc.meme.auth.domain.user.controller;

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

    @PostMapping("/api/v0/auth/model/signup")
    public BaseResponseDto<UserResponse.JoinSuccessDto> modelSignUp(@RequestBody UserRequest.ModelJoinDto joinDto) {
        return BaseResponseDto.SuccessResponse(SuccessStatus.MODEL_JOIN_SUCCESS, userService.modelSignUp(joinDto));
    }

    @PostMapping("/api/v0/auth/artist/signup")
    public BaseResponseDto<UserResponse.JoinSuccessDto> artistSignUp(@RequestBody UserRequest.ArtistJoinDto joinDto) {
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_JOIN_SUCCESS, userService.artistSignUp(joinDto));
    }

    @PostMapping("/api/v0/auth/artist/extra")
    public BaseResponseDto<?> artistExtra(@RequestBody UserRequest.ArtistExtraDto joinDto) {
        userService.artistExtra(joinDto);
        return BaseResponseDto.SuccessResponse(SuccessStatus.ARTIST_EXTRA_JOIN_SUCCESS);
    }
}
