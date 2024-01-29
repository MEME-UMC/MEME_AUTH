package umc.meme.auth.domain.token.domain;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import umc.meme.auth.domain.token.domain.RefreshToken;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private RedisTemplate redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRES = 7 * 24 * 60 * 60;


    public RefreshTokenRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getAccessToken());
        redisTemplate.expire(refreshToken.getRefreshToken(), REFRESH_TOKEN_EXPIRES, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String accessToken = valueOperations.get(refreshToken);

        if (Objects.isNull(accessToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, accessToken));
    }

    public void delete(RefreshToken refreshToken){
        redisTemplate.delete(refreshToken.getRefreshToken());
    }
}
