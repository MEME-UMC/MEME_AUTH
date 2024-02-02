package umc.meme.auth.global.exception.handler;


import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.exception.GeneralException;

public class AritstHandler extends GeneralException {
    public AritstHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
