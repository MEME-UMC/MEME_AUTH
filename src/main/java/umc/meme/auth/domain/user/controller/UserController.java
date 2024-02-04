package umc.meme.auth.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.meme.auth.domain.user.dto.UserRequest;
import umc.meme.auth.domain.user.service.UserService;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "모델 회원가입", description = "모델이 회원가입할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/model/signup")
    public ResponseEntity<?> modelSignUp(@RequestBody UserRequest.ModelJoinDto joinDto) {
        return ResponseEntity.ok(userService.modelSignUp(joinDto));
    }

    @Operation(summary = "아티스트 회원가입", description = "아티스트가 회원가입할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/artist/signup")
    public ResponseEntity<?> artistSignUp(@RequestBody UserRequest.ArtistJoinDto joinDto) {
        return ResponseEntity.ok(userService.artistSignUp(joinDto));
    }

    @Operation(summary = "아티스트 추가 회원가입", description = "아티스트가 회원가입하고 추가 정보를 입력할때 사용하는 API입니다.")
    @PostMapping("/api/v0/auth/artist/extra")
    public ResponseEntity<?> artistExtra(@RequestBody UserRequest.ArtistExtraDto joinDto) {
        userService.artistExtra(joinDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
