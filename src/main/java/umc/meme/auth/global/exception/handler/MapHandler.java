package umc.meme.auth.global.exception.handler;

import umc.meme.auth.global.common.BaseErrorCode;
import umc.meme.auth.global.exception.GeneralException;

public class MapHandler extends GeneralException {
  public MapHandler(BaseErrorCode errorCode) {super(errorCode);}
}
