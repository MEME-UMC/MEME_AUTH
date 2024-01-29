package umc.meme.auth.global.exception.handler;

import org.springframework.security.core.AuthenticationException;
import umc.meme.auth.global.common.status.ErrorStatus;

public class JwtHandler extends AuthenticationException {
    public JwtHandler(ErrorStatus status){
        super(status.name());
    }
}
