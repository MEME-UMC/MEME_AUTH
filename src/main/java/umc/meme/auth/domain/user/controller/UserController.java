package umc.meme.auth.domain.user.controller;

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

//    @PostMapping("/api/v0/auth/signup")
//    public ResponseEntity<?> signup(@RequestBody UserRequest.JoinDto joinDto) {
//        userService.signup(joinDto);
//        return ResponseEntity.ok(HttpStatus.OK);
//    }

    @PostMapping("/api/v0/auth/model/signup")
    public ResponseEntity<?> modelSignUp(@RequestBody UserRequest.modelJoinDto joinDto) {
        userService.modelSignUp(joinDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/v0/auth/artist/signup")
    public ResponseEntity<?> artistSignUp(@RequestBody UserRequest.artistJoinDto joinDto) {
        userService.artistSignUp(joinDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/api/v0/auth/artist/extra")
    public ResponseEntity<?> artistExtra(@RequestBody UserRequest.artistExtraDto joinDto) {
        userService.artistExtra(joinDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
