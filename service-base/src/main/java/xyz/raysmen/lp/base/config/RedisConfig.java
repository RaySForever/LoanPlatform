package xyz.raysmen.lp.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;

/**
 * RedisConfig
 * 自定义Redis配置类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.base.config
 * @date 2022/05/26 21:47
 */
@Slf4j
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(CacheProperties cacheProperties, RedisTemplate<String, Object> redisTemplate){
        // 基本配置
        log.info("正在设置自定义redis缓存配置");
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))
                // 设置value 为自动转Json的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));

        // 依据yaml缓存配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }

        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());

        // 构造一个redis缓存管理器
        log.info("正在配置自定义redis缓存管理器");
        return RedisCacheManager.RedisCacheManagerBuilder
                // Redis 连接工厂，使用连接池
                .fromConnectionFactory(connectionFactory)
                // 缓存配置以及配置同步修改或删除 put/evict
                .cacheDefaults(config).transactionAware().build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

        log.info("开始初始化自定义的RedisTemplate<String, Object>");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 首先解决key的序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);

        // 解决value的序列化方式
        redisTemplate.setValueSerializer(getJackson2JsonRedisSerializer());
        log.info("已设置好RedisTemplate的key和value的序列化方式");
        return redisTemplate;
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，避免程序终止
        log.info("初始化Redis的CacheErrorHandler");
        return new CacheErrorHandler() {

            /**
             * Handle the given runtime exception thrown by the cache provider when
             * retrieving an item with the specified {@code key}, possibly
             * rethrowing it as a fatal exception.
             *
             * @param exception the exception thrown by the cache provider
             * @param cache     the cache
             * @param key       the key used to get the item
             * @see Cache#get(Object)
             */
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]; exception -> {}", key, ExceptionUtils.getStackTrace(exception));
            }

            /**
             * Handle the given runtime exception thrown by the cache provider when
             * updating an item with the specified {@code key} and {@code value},
             * possibly rethrowing it as a fatal exception.
             *
             * @param exception the exception thrown by the cache provider
             * @param cache     the cache
             * @param key       the key used to update the item
             * @param value     the value to associate with the key
             * @see Cache#put(Object, Object)
             */
            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]; exception -> {}", key, ExceptionUtils.getStackTrace(exception));
            }

            /**
             * Handle the given runtime exception thrown by the cache provider when
             * clearing an item with the specified {@code key}, possibly rethrowing
             * it as a fatal exception.
             *
             * @param exception the exception thrown by the cache provider
             * @param cache     the cache
             * @param key       the key used to clear the item
             */
            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]; exception -> {}", key, ExceptionUtils.getStackTrace(exception));
            }

            /**
             * Handle the given runtime exception thrown by the cache provider when
             * clearing the specified {@link Cache}, possibly rethrowing it as a
             * fatal exception.
             *
             * @param exception the exception thrown by the cache provider
             * @param cache     the cache to clear
             */
            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("Redis occur handleCacheClearError：{}", ExceptionUtils.getStackTrace(exception));
            }
        };
    }

    private Jackson2JsonRedisSerializer<Object> getJackson2JsonRedisSerializer() {
        // 解决value的序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // 序列化时将类的数据类型存入json，以便反序列化的时候转换成正确的类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        return jackson2JsonRedisSerializer;
    }
}
