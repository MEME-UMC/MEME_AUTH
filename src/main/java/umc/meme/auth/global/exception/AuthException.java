package umc.meme.auth.global.exception;

import umc.meme.auth.global.common.BaseErrorCode;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
