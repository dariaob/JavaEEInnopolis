package repository;

import entity.DoctorEntity;
import entity.PatientCardEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorRepository {
    // Выводит все данные таблицы
    List<DoctorEntity> findAll();
    // Создает новую запись
    int create(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId) throws RecordExistsException, ObjectNotFountException;
    // Находит по id
    DoctorEntity findById(Long id) throws ObjectNotFountException;
    // Обновляет запись
    void update(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId, Long id) throws RecordExistsException, ObjectNotFountException;
    // Удаляет по id
    int deleteById(Long id) throws ObjectNotFountException, ImpossibleToDeleteException;
    // Удаляет все записи таблицы
    int deletAll() throws ObjectNotFountException;
    List<DoctorEntity> findByOffice(Long officeId);
}
