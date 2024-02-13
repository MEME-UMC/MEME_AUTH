package umc.meme.auth.global.infra;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import umc.meme.auth.global.oauth.jwk.PublicKeyDto;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {

    private RedisTemplate redisTemplate;
    private static final long CACHING_EXPIRES = 60 * 60;  // 60분

    public RedisRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(PublicKeyDto publicKeyDto) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(publicKeyDto.getProvider(), publicKeyDto.getKey());
        redisTemplate.expire(publicKeyDto.getProvider(), CACHING_EXPIRES, TimeUnit.SECONDS);
    }

    public Optional<PublicKeyDto> findPublicKey(String provider) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = valueOperations.get(provider);

        return Optional.of(new PublicKeyDto(provider, key));
    }
}
