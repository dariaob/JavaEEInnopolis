package org.dariaob.models;
import jakarta.persistence.*;
import lombok.*;

/**
 * Таблица: doctor_specializations
 * Связь между врачами (doctors) и специализациями (specializations)
 */
@Entity
@Table(name = "doctor_specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSpecializations {

    /**
     * Составной первичный ключ:
     * - doctor_id       -> ID врача
     * - specialization_id -> ID специализации
     */
    @EmbeddedId
    private DoctorSpecializationId id;

    /**
     * Связанный врач
     * Связь с таблицей doctors через doctor_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctors doctor;

    /**
     * Связанная специализация
     * Связь с таблицей specializations через specialization_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("specializationId")
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specializations specialization;
}

