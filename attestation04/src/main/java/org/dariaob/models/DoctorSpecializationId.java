package org.dariaob.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Составной ключ для таблицы doctor_specializations
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSpecializationId implements Serializable {

    /**
     * ID врача
     */
    private Long doctorId;

    /**
     * ID специализации
     */
    private Long specializationId;
}
