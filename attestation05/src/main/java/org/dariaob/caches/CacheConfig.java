package org.dariaob.caches;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
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

/**
 * Конфигурация менеджера кэша для приложения attestation05
 */
@EnableCaching
@Configuration
public class CacheConfig {

    /**
     * Настройка Redis CacheManager с ограничениями по времени
     *
     * @return the cache manager
     */

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // Настройка ObjectMapper для Redis
        ObjectMapper redisMapper = objectMapper.copy();
        redisMapper.activateDefaultTyping(redisMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        // Создание сериализатора с поддержкой JavaTime
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("doctorSchedule", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("patientsAllActive", defaultConfig);
        cacheConfigs.put("patientsById", defaultConfig);
        cacheConfigs.put("patientsByPhone", defaultConfig);
        cacheConfigs.put("patientCardsAllActive", defaultConfig);
        cacheConfigs.put("patientCardsById", defaultConfig);
        cacheConfigs.put("patientCardsByPatientId", defaultConfig);
        cacheConfigs.put("patientCardsByDiagnosis", defaultConfig);
        cacheConfigs.put("patientCardsHistoryByCardId", defaultConfig);
        cacheConfigs.put("patientCardsLastChangeByCardId", defaultConfig);
        cacheConfigs.put("doctors", defaultConfig);
        cacheConfigs.put("appointments", defaultConfig);
        cacheConfigs.put("specializations", defaultConfig);
        cacheConfigs.put("offices", defaultConfig);
        cacheConfigs.put("doctorSpecializations", defaultConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
