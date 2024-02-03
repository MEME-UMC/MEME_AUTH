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

    @PostMapping("/api/v0/auth/model/signup")
    public ResponseEntity<?> modelSignUp(@RequestBody UserRequest.ModelJoinDto joinDto) {
        return ResponseEntity.ok(userService.modelSignUp(joinDto));
    }

    @PostMapping("/api/v0/auth/artist/signup")
    public ResponseEntity<?> artistSignUp(@RequestBody UserRequest.ArtistJoinDto joinDto) {
        return ResponseEntity.ok(userService.artistSignUp(joinDto));
    }

    @PostMapping("/api/v0/auth/artist/extra")
    public ResponseEntity<?> artistExtra(@RequestBody UserRequest.ArtistExtraDto joinDto) {
        userService.artistExtra(joinDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
