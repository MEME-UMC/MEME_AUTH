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
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    // S3Service ERRor
    S3_NOT_CONVERTABLE(HttpStatus.NOT_MODIFIED, "S33004", "변환이 안됩니다"),
    S3_UPLOAD_FAILED(HttpStatus.FORBIDDEN, "S34003", "S3 업로드에 실패하셨습니다"),
    S3_WRONG_PATH(HttpStatus.BAD_REQUEST, "S34000", "S3 요청 Path가 잘못되었습니다"),
    S3_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "S34001", "S3 URL을 가져올 수 없습니다"),
    S3_DELETE_FAILED(HttpStatus.FORBIDDEN, "S34003", "S3 삭제에 실패하셨습니다"),
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "테스트"),
    //JWT 토큰 관련 에러
    JWT_BAD_REQUEST(HttpStatus.BAD_REQUEST,"TOKEN400","잘못된 JWT 서명입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN401","유효한 JWT 토큰이 없습니다"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"TOKEN402","액세스 토큰이 만료되었습니다"),
    JWT_TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED,"TOKEN403","지원하지 않는 JWT 토큰입니다");

    private final HttpStatus httpStatus;
    private final String code;
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
