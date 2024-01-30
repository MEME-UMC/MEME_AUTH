package umc.meme.auth.global.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.common.ErrorReasonDto;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    //일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,400,"잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,401,"인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, 403, "금지된 요청입니다."),

    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 401, "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, 402, "닉네임은 필수 입니다."),

    // Artist 관련 에러
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, 401, "Artist 정보가 존재하지 않습니다."),

    //Model 관련 에러
    MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, 401, "Model 정보가 존재하지 않습니다."),

    //Oauth Error
    KAKAO_ACCESS_TOKEN_ERROR(HttpStatus.NOT_FOUND, 401, "Access Token이 존재하지 않습니다"),

    //JWT 토큰 관련 에러
    JWT_BAD_REQUEST(HttpStatus.BAD_REQUEST,400,"잘못된 JWT 서명입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 401,"유효한 JWT 토큰이 없습니다"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,402,"액세스 토큰이 만료되었습니다"),
    JWT_TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED,403,"지원하지 않는 JWT 토큰입니다");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    @Override
    public ErrorReasonDto getReason(){
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .success(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus(){
        return ErrorReasonDto.builder()
                .httpStatus(httpStatus)
                .message(message)
                .code(code)
                .success(false)
                .build();
    }
}
