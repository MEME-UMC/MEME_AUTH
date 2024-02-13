package umc.meme.auth.global.oauth.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.domain.user.entity.UserRepository;
import umc.meme.auth.global.oauth.AuthService;

@RequiredArgsConstructor
@Service
public class AppleAuthService {

    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.apple.issuer}")
    private String issuer;

    @Value("${spring.security.oauth2.apple.client-id}")
    private String restApiKey;
}

