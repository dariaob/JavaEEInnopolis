package repository;

import entity.PatientCardEntity;
import entity.PatientEntity;

import java.util.List;

public interface PatientRepository {
    List<PatientEntity> findAll();
    int insertRow(Long insuranceId, String name, String address, Long cardId);
    int deleteRow(Long id);
}
