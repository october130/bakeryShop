package com.cake.platform.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        // 无类型信息的 ObjectMapper，用于兼容旧格式数据
        ObjectMapper plainOm = new ObjectMapper();
        plainOm.registerModule(new JavaTimeModule());
        plainOm.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisSerializer<Object> jsonSerializer = new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object value) throws SerializationException {
                if (value == null) {
                    return new byte[0];
                }
                try {
                    return om.writeValueAsBytes(value);
                } catch (JsonProcessingException e) {
                    throw new SerializationException("Could not serialize: " + e.getMessage(), e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                try {
                    return om.readValue(bytes, Object.class);
                } catch (IOException e) {
                    // 回退：兼容旧版本未带类型信息的纯 JSON 数据
                    try {
                        return plainOm.readValue(bytes, Object.class);
                    } catch (IOException ex) {
                        throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
                    }
                }
            }
        };
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
