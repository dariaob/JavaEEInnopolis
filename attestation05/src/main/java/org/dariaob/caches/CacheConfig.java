package org.dariaob.caches;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

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
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("doctorSchedule", defaultConfig);
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
