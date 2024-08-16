package org.silluck.domain.order.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.silluck.domain.order.exception.CustomException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static org.silluck.domain.order.exception.ErrorCode.WISHLIST_CHANGE_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;
    // 템플릿을 컨버팅 할 수 있는 util성 클래스 만들어서 사용
    private static final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(Long key, Class<T> classType) {
        return get(key.toString(), classType);
    }

    // key가 String, Long 전부 받을 수 있게
    private <T> T get(String key, Class<T> classType) {
        String redisValue = (String) redisTemplate.opsForValue().get(key);

        if (ObjectUtils.isEmpty(redisValue)) {
            return null;
        } else {
            try {
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.error("Parsing Error", e);
                return null;
            }
        }
    }

    public void put(Long key, Wishlist wishlist) {
        put(key.toString(), wishlist);
    }

    private void put(String key, Wishlist wishlist) {
        try {
            redisTemplate.opsForValue().set(key, mapper.writeValueAsString(wishlist));
        } catch (JsonProcessingException e) {
            throw new CustomException(WISHLIST_CHANGE_FAIL);
        }
    }
}