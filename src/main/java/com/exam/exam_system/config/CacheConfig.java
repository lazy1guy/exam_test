package com.exam.exam_system.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 首页数据缓存1小时
        cacheConfigurations.put("homeDataCache", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)));

        // 成绩数据缓存30分钟
        cacheConfigurations.put("scoreSummaryCache", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)));

        // 考试详情缓存2小时（较少变化）
        cacheConfigurations.put("examDetailCache", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2)));

        // 通知缓存特殊配置（15分钟）
        cacheConfigurations.put("unreadNotifications", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues());

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10)) // 默认配置
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())))
                .transactionAware()
                .build();
    }
}
