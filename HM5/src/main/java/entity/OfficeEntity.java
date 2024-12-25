package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Данные кабинета приема
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OfficeEntity {
    // Номер(id) кабинета
    private Long id;
    // Назначение кабинета
    private String officeType;
}
