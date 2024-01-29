package umc.meme.auth.global.exception.handler;


import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.exception.GeneralException;

public class MemberHandler extends GeneralException {
    public MemberHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
