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
 * Репозиторий специализаций врачей
 */
@Repository
public interface DoctorSpecializationsRepository extends JpaRepository<DoctorSpecializations, DoctorSpecializationId> {

    // Проверка существования по составному ключу
    boolean existsByIdDoctorIdAndIdSpecializationId(Long doctorId, Long specializationId);

    // Получить все связи врача по doctorId
    List<DoctorSpecializations> findAllByIdDoctorId(Long doctorId);

    // Удалить связь врача и специализации
    @Transactional
    @Modifying
    @Query("DELETE FROM DoctorSpecializations ds WHERE ds.id.doctorId = ?1 AND ds.id.specializationId = ?2")
    void deleteByDoctorIdAndSpecializationId(Long doctorId, Long specializationId);

    // Найти все связи по specializationId
    List<DoctorSpecializations> findAllByIdSpecializationId(Long specializationId);
}
