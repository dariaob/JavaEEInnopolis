package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Таблица: doctors
 * Врачи медицинского учреждения
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Doctors {
    // Уникальный идентификатор врача
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Полное имя врача
    @Column(nullable = false, length = 30)
    private String name;

    // Контактный телефон
    @Column(nullable = false, length = 20, unique = true)
    private String phone; // Контактный телефон

    // Время начала рабочего дня
    @Column(name = "work_hours_from", nullable = false)
    private LocalDateTime workHoursFrom;

    // Время окончания рабочего дня
    @Column(name = "work_hours_for", nullable = false)
    private LocalDateTime workHoursFor;

    // Кабинет, закреплённый за врачом
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_id", nullable = false)
    private Offices office;

    // Флаг удаления
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Специализации врача
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<DoctorSpecializations> doctorSpecializations = new HashSet<>();

    public Doctors(long l, String doc, Object o, Object o1, boolean b) {
    }

    /**
     * Метод для добавления специализации врачу
     */
    public void addSpecialization(Specializations specialization) {
        DoctorSpecializations ds = new DoctorSpecializations();
        ds.setDoctor(this);
        ds.setSpecialization(specialization);
        this.doctorSpecializations.add(ds);
    }
}
