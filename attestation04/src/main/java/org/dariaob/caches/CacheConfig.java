package org.dariaob.caches;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация менеджера кэша для приложения attestation03
 */
@EnableCaching
@Configuration
public class CacheConfig {

    /**
     * Настройка Caffeine CacheManager с ограничениями по времени и размеру.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "patients",
                "doctors",
                "appointments",
                "specializations",
                "offices",
                "doctorSchedule",
                "doctorSpecializations",
                "patientCardsHistory",

                // кэши для PatientCardsService
                "patientCardsAllActive",
                "patientCardsById",
                "patientCardsByPatientId",
                "patientCardsByDiagnosis",
                "patientCardsHistoryByCardId",
                "patientCardsLastChangeByCardId",

                // PatientsService
                "patientsAllActive",
                "patientsById",
                "patientsByPhone"
        );

        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(200));

        return manager;
    }
}
