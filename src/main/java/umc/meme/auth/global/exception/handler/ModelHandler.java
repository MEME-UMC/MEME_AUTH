package umc.meme.auth.global.exception.handler;


import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.exception.GeneralException;

public class ModelHandler extends GeneralException {
    public ModelHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
