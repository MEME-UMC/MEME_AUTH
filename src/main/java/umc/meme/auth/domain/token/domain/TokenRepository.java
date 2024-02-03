package umc.meme.auth.domain.token.domain;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class TokenRepository {

    private RedisTemplate redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRES = 7 * 24 * 60 * 60;


    public TokenRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Token token) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token.getAccessToken(), token.getRefreshToken());
        redisTemplate.expire(token.getAccessToken(), REFRESH_TOKEN_EXPIRES, TimeUnit.SECONDS);
    }

    public Optional<Token> findByAccessToken(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(accessToken);

        return Optional.of(new Token(accessToken, refreshToken));
    }

    public void delete(Token token) {
        redisTemplate.delete(token.getAccessToken());
    }
}
