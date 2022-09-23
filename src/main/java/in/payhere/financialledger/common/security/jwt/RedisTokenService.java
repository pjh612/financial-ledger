package in.payhere.financialledger.common.security.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisTokenService implements TokenService {

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public TokenResponse findByUserId(Long userId) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();

		return new TokenResponse(userId.toString(), values.get(userId));
	}

	@Override
	public String save(Long userId, String token, Long expirySeconds) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(userId.toString(), token, expirySeconds);

		return token;
	}
}
