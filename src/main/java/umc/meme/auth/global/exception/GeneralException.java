package umc.meme.auth.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.common.ErrorReasonDto;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseErrorCode baseErrorCode;

    public ErrorReasonDto getReason(){
        return this.baseErrorCode.getReason();
    }

    public ErrorReasonDto getReasonHttpStatus(){
        return this.baseErrorCode.getReasonHttpStatus();
    }

}
