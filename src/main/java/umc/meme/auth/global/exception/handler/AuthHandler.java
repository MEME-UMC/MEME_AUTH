package umc.meme.auth.global.exception.handler;

import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.exception.GeneralException;

public class AuthHandler extends GeneralException {
    public AuthHandler(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
