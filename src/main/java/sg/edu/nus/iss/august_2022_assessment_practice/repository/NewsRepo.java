package sg.edu.nus.iss.august_2022_assessment_practice.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.august_2022_assessment_practice.constant.Constant;

@Repository
public class NewsRepo {
    
    @Autowired
    @Qualifier(Constant.TEMPLATE02)
    RedisTemplate<String, String> redisTemplate;

    public void create(String redisKey, String hashKey, String hashValue) {
        redisTemplate.opsForHash().put(redisKey, hashKey, hashValue);
    }


    public Map<Object, Object> getEntries(String redisKey){
        return redisTemplate.opsForHash().entries(redisKey);
    }


    public List<Object> getValues(String redisKey) {
        return redisTemplate.opsForHash().values(redisKey);
    }
    

    public Boolean hasHashKey(String redisKey, String hashKey){
        return redisTemplate.opsForHash().hasKey(redisKey, hashKey);
    }

    
    public Object get(String redisKey, String hashKey) {
        // Object can be anything, a string, a number, a class, a collection
        return redisTemplate.opsForHash().get(redisKey, hashKey);
    }
    
}
