package umc.meme.auth.global.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    // UserController
    MODEL_JOIN_SUCCESS(HttpStatus.OK, 200, "회원가입이 완료되었습니다"),
    ARTIST_JOIN_SUCCESS(HttpStatus.OK, 200, "회원가입이 완료되었습니다"),
    ARTIST_EXTRA_JOIN_SUCCESS(HttpStatus.OK, 200, "추가 회원 정보 기입이 완료되었습니다."),
    USER_EXISTS(HttpStatus.OK, 200, "사용자 정보가 확인되었습니다."),
    USER_NOT_EXISTS(HttpStatus.OK, 200, "등록되지 않은 사용자입니다."),


    // AuthController
    LOGIN_SUCCESS(HttpStatus.OK,200,"토큰 검증에 성공하였습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, 200, "토큰 재발급이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, 200, "Logout Success"),
    WITHDRAW_SUCCESS(HttpStatus.OK, 200, "Withdraw Success");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
