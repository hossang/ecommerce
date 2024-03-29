package hochang.ecommerce.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import hochang.ecommerce.dto.BulletinItem;
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
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static hochang.ecommerce.constants.CacheConstants.*;
import static hochang.ecommerce.constants.NumberConstants.*;


@Configuration
public class RedisCacheConfig {
    @Value("${spring.cache-redis.host}")
    private String host;

    @Value("${spring.cache-redis.port}")
    private int port;

    @Bean
    @Primary
    public RedisConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public CacheManager cacheManager(ObjectMapper redisObjectMapper) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)))
                .entryTtl(Duration.ofMinutes(LONG_30))
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.simple());

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(FIND_MAIN_ITEMS_WITH_COVERING_INDEX, redisCacheConfiguration.entryTtl(Duration.ofMinutes(LONG_1)));
        cacheConfigurations.put(FIND_BULLETIN_ITEM, redisCacheConfiguration.entryTtl(Duration.ofDays(LONG_1)));


        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(cacheRedisConnectionFactory())
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean("cacheRedisTemplate")
    public RedisTemplate<String, Object> cacheRedisTemplate(ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cacheRedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        return redisTemplate;
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }
}
