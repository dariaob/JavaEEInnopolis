package entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Кабинет врача
 */
@Data
@NoArgsConstructor
@ToString
public class OfficeEntity {
    // Номер(id) кабинета
    private Long id;
    // Назначение кабинета
    private String officeType;

    public OfficeEntity(Long id, String officeType) {
        this.id = id;
        this.officeType = officeType;
    }
}
