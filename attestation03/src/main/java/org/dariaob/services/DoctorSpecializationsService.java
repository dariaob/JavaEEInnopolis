package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.DoctorSpecializations;
import org.dariaob.repositories.DoctorSpecializationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы со связями врачей и специализаций.
 */
@Service
@RequiredArgsConstructor
public class DoctorSpecializationsService {

    private final DoctorSpecializationsRepository doctorSpecializationsRepository;

    /**
     * Получить все специализации врача по ID врача.
     *
     * @param doctorId ID врача
     * @return список специализаций
     */
    public List<DoctorSpecializations> getSpecializationsByDoctorId(Long doctorId) {
        return doctorSpecializationsRepository.findAllByIdDoctorId(doctorId);
    }

    /**
     * Удалить связь врача со специализацией.
     *
     * @param doctorId         ID врача
     * @param specializationId ID специализации
     * @throws DataNotFoundException если связь не найдена
     */
    @Transactional
    public void deleteSpecialization(Long doctorId, Long specializationId) {
        if (!doctorSpecializationsRepository.existsByIdDoctorIdAndIdSpecializationId(doctorId, specializationId)) {
            throw new DataNotFoundException("Связь врача с специализацией не найдена.");
        }
        doctorSpecializationsRepository.deleteByDoctorIdAndSpecializationId(doctorId, specializationId);
    }

    /**
     * Сохранить связь врача со специализацией.
     * @param doctorSpecialization
     * @return
     */
    @Transactional
    public DoctorSpecializations save(DoctorSpecializations doctorSpecialization) {
        return doctorSpecializationsRepository.save(doctorSpecialization);
    }

    /**
     * Проверить, существует ли связь врача со специализацией.
     * @param doctorId
     * @param specializationId
     * @return
     */
    public boolean existsByDoctorAndSpecialization(Long doctorId, Long specializationId) {
        return doctorSpecializationsRepository
                .existsByIdDoctorIdAndIdSpecializationId(doctorId, specializationId);
    }
}
