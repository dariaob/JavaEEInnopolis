package repository;

import entity.ReceptionEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Прием
 */
public interface ReceptionRepository {
    // Выводит все данные таблицы
    List<ReceptionEntity> findAll();
    // Создает новую запись
    int create(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId);
    // Находит по id
    ReceptionEntity findById(Long id) throws ObjectNotFountException;
    // Обновляет запись
    void update(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId, Long id);
    // Удаляет по id
    int deleteById(Long id, Long doctorId, Long officeId, Long patientId, Long cardId) throws ObjectNotFountException, ImpossibleToDeleteException;
    // Удаляет все записи таблицы
    int deletAll() throws ObjectNotFountException, ImpossibleToDeleteException;
    List<ReceptionEntity> findByDoctorId(Long doctorId);
}
