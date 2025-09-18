package com.techtack.blue.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        
        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer with JavaTimeModule for values
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Create JSON serializer with proper Java 8 time support
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();
        
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10)) // Default TTL of 10 minutes
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(jsonSerializer));
        
        // Configure specific cache TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Market snapshot cache - short TTL (30 seconds) for real-time data
        cacheConfigurations.put("marketSnapshot", 
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));
        
        // Market indices cache - 1 minute TTL
        cacheConfigurations.put("marketIndices", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        
        // Industry heatmap - 5 minutes TTL
        cacheConfigurations.put("industryHeatmap", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Top stocks caches - 1 minute TTL
        cacheConfigurations.put("topVolume", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("topGainers", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("topLosers", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        
        // Add configurations for new unified caches
        cacheConfigurations.put("marketOverview", 
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));
        cacheConfigurations.put("unifiedMarketSnapshot", 
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));
        cacheConfigurations.put("unifiedMarketIndices", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("unifiedTopGainers", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("unifiedTopLosers", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("unifiedTopVolume", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("unifiedIndustryHeatmap", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("mostActive", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("sectorPerformance", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("stockQuote", 
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));
        cacheConfigurations.put("intradayData", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
    
    /**
     * Create a JSON serializer with proper configuration for Java 8 time types
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register JavaTimeModule to handle LocalDateTime and other Java 8 time types
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configure type information for polymorphic types
        objectMapper.activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
