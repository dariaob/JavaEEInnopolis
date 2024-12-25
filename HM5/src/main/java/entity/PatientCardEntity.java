package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Карточка пациента
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatientCardEntity {

   // Номер(id) карточки пациента
   private Long id;
   // Жалобы, симптомы
   private String symptoms;
   // Диагноз
   private String diagnosis;
   // Лекарства
   private String medicine;
}
