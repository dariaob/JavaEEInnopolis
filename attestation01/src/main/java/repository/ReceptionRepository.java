package repository;

import entity.ReceptionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceptionRepository {
    List<ReceptionEntity> findAll();
    Long insertRow(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId);
    int deleteRow(Long id);
}
