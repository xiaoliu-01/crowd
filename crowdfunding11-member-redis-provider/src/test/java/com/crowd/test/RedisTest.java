package com.crowd.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
   
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Test
	public void testRedis() {
		ValueOperations<String, String> value = redisTemplate.opsForValue();
//		String string = value.get("k1");
//		System.out.println(string);
		value.set("idd", "green");
	}
	
	
 }
