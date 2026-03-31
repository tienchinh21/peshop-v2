package xjanua.backend.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    // ======================================
    // BASIC STRING
    // ======================================
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, String value, long seconds) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    // ======================================
    // OBJECT (save as JSON string)
    // ======================================
    public <T> boolean setObject(String key, T data, long seconds) {
        try {
            String json = objectMapper.writeValueAsString(data);
            stringRedisTemplate.opsForValue().set(key, json, seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null)
                return null;
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    // ======================================
    // LIST OBJECT (save as JSON string)
    // ======================================
    public <T> boolean setList(String key, List<T> data, long seconds) {
        try {
            String json = objectMapper.writeValueAsString(data);
            stringRedisTemplate.opsForValue().set(key, json, seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null)
                return null;

            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }
}
