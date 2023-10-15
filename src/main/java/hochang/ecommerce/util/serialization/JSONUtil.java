package hochang.ecommerce.util.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JSONUtil {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    public <T> boolean saveData(String key, T data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(data);
            cacheRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }

    }

    public <T> Optional<T> getData(String key, Class<T> classType) {
        String jsonResult = (String) cacheRedisTemplate.opsForValue().get(key);

        if(jsonResult == null){
            return Optional.empty();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper(); //얘를 private final로 빼도 될까?
            return Optional.of(objectMapper.readValue(jsonResult, classType));
        } catch(Exception e){
            return Optional.empty();
        }
    }
}
