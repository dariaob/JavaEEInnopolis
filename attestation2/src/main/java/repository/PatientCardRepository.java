package repository;

import entity.PatientCardEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;

import java.util.List;

/**
 * Карточка пациента
 */
public interface PatientCardRepository {
    // Выводит все данные таблицы
    List<PatientCardEntity> findAll();
    // Создает новую запись
    int create(String symptoms, String diagnosis, String medicine) throws RecordExistsException, ObjectNotFountException;
    // Находит по id
    PatientCardEntity findById(Long id) throws ObjectNotFountException;
    // Обновляет запись
    void update(String symptoms, String diagnosis, String medicine, Long id) throws RecordExistsException, ObjectNotFountException;
    // Удаляет по id
    int deleteById(Long id) throws ObjectNotFountException, ImpossibleToDeleteException;
    // Удаляет все записи таблицы
    int deletAll();
    List<PatientCardEntity> findByDiagnosis(String diagnosis);
}
