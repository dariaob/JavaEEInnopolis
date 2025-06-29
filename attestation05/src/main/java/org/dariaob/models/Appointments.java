package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Сущность "Запись на приём".
 * Хранит данные о приёме пациента у врача, включая дату, врача, пациента, кабинет и другие детали.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Appointments implements Serializable {

    /**
     * Уникальный идентификатор записи на приём.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время приёма.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Врач, к которому записан пациент.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    private Doctors doctor;

    /**
     * Пациент, записанный на приём.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    private Patients patient;

    /**
     * Планируемое время начала приёма.
     */
    @Column(name = "work_hours_from", nullable = false)
    private LocalDateTime workHoursFrom;

    /**
     * Планируемое время окончания приёма.
     */
    @Column(name = "work_hours_for", nullable = false)
    private LocalDateTime workHoursFor;

    /**
     * Флаг, указывающий на мягкое удаление записи.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * Медицинская карта, связанная с приёмом.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    @ToString.Exclude
    private PatientCards card;

    /**
     * Номер страхового полиса, использованный при записи.
     */
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;

    /**
     * Кабинет, в котором состоится приём.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_id", nullable = false)
    @ToString.Exclude
    private Offices office;

    /**
     * Интервал/слот в расписании
     */
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private DoctorScheduleSlot slot;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Appointments that = (Appointments) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
