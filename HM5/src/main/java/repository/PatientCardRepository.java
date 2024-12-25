package repository;

import entity.PatientCardEntity;

import java.util.List;

public interface PatientCardRepository {
    List<PatientCardEntity> findAll();
    void insertRow(Long id, String symptoms, String diagnosis, String medicine);
    int deleteRow(Long id);
}
