package repository;

import entity.PatientEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;

import java.util.List;

/**
 * Данные пациента
 */
public interface PatientRepository {
    // Выводит все данные таблицы
    List<PatientEntity> findAll();
    // Создает новую запись
    int create(Long insuranceId, String name, String address, Long cardId);
    // Находит по id
    PatientEntity findById(Long patientId) throws ObjectNotFountException;
    // Обновляет запись
    void update(Long insuranceId, String name, String address, Long cardId, Long patientId) throws  ObjectNotFountException;
    // Удаляет по id
    int deleteById(Long patientId, Long cardId) throws ObjectNotFountException, ImpossibleToDeleteException;
    // Удаляет все записи таблицы
    int deletAll() throws ObjectNotFountException;
    List<PatientEntity> findByDInsuranceId(Long insuranceId);
}
