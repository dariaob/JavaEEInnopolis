package repository;
import entity.OfficeEntity;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;

import java.util.List;

/**
 * Кабинет врача
 */
public interface OfficeRepository {
    List<OfficeEntity> findAll();
    int create(String officeType) throws RecordExistsException, ObjectNotFountException;
    OfficeEntity findById(Long id) throws ObjectNotFountException;
    void update(String officeType, Long id) throws RecordExistsException, ObjectNotFountException;
    void deleteById(Long id) throws ObjectNotFountException;
    void deletAll() throws ObjectNotFountException;
    List<OfficeEntity> findByOfficeType(String officeType);
}
