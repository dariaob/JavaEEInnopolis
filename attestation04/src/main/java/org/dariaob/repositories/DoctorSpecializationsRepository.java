package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.DoctorSpecializationId;
import org.dariaob.models.DoctorSpecializations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с медицинскими специализациями врачей.
 */
@Repository
public interface DoctorSpecializationsRepository extends JpaRepository<DoctorSpecializations, DoctorSpecializationId> {

    /**
     * Проверить, существует ли связь врача и специализации по составному ключу.
     *
     * @param doctorId         идентификатор врача
     * @param specializationId идентификатор специализации
     * @return true, если связь существует, иначе false
     */
    boolean existsByIdDoctorIdAndIdSpecializationId(Long doctorId, Long specializationId);

    /**
     * Получить все связи врача по его идентификатору.
     *
     * @param doctorId идентификатор врача
     * @return список связей врача с специализациями
     */
    List<DoctorSpecializations> findAllByIdDoctorId(Long doctorId);

    /**
     * Удалить связь врача и специализации по их идентификаторам.
     *
     * @param doctorId         идентификатор врача
     * @param specializationId идентификатор специализации
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM DoctorSpecializations ds WHERE ds.id.doctorId = ?1 AND ds.id.specializationId = ?2")
    void deleteByDoctorIdAndSpecializationId(Long doctorId, Long specializationId);

    /**
     * Найти все связи по идентификатору специализации.
     *
     * @param specializationId идентификатор специализации
     * @return список связей с врачами
     */
    List<DoctorSpecializations> findAllByIdSpecializationId(Long specializationId);
}
