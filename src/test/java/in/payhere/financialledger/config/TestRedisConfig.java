package in.payhere.financialledger.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import in.payhere.financialledger.common.config.properties.RedisConfigProperties;
import redis.embedded.RedisServer;

@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisConfigProperties.class)
public class TestRedisConfig {
	private RedisServer redisServer;
	private RedisConfigProperties redisConfigProperties;

	public TestRedisConfig(RedisConfigProperties redisConfigProperties) {
		this.redisConfigProperties = redisConfigProperties;
		redisServer = new RedisServer(redisConfigProperties.port());
	}

	@PostConstruct
	public void startRedis() throws IOException {
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		redisServer.stop();
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisConfigProperties.host(), redisConfigProperties.port());
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}
}
