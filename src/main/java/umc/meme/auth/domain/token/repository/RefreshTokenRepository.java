package umc.meme.auth.domain.token.repository;

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


    public RefreshTokenRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getMemberId());
        redisTemplate.expire(refreshToken.getRefreshToken(), REFRESH_TOKEN_EXPIRES, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long memberId = valueOperations.get(refreshToken);

        if (Objects.isNull(memberId)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, memberId));
    }

    public void delete(final RefreshToken refreshToken){
        redisTemplate.delete(refreshToken.getRefreshToken());
    }
}
