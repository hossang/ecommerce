package hochang.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


@Configuration
public class RedisCacheConfig {
    @Value("${spring.cache-redis.host}")
    private String host;

    @Value("${spring.cache-redis.port}")
    private int port;

    @Value("${spring.cache-redis.ttl}")
    private long ttl;

    @Bean
    @Primary
    public RedisConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) //이건 이슈 없나? 있을거같은데
                .entryTtl(Duration.ofMinutes(ttl))
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.simple());
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(cacheRedisConnectionFactory())
                .cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean("cacheRedisTemplate")
    public RedisTemplate<String, Object> cacheRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cacheRedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
