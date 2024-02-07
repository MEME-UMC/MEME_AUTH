package umc.meme.auth.global.oauth;

import umc.meme.auth.domain.user.entity.User;

public interface AuthService {
    public User getUserInfo(String idToken);
}
