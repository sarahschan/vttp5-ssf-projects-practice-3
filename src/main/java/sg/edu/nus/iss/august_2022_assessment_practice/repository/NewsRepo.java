package sg.edu.nus.iss.august_2022_assessment_practice.repository;

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

}
