package repository;
import entity.OfficeEntity;
import exceptions.ObjectNotFountException;

import java.util.List;

/**
 * Кабинет врача
 */
public interface OfficeRepository {
    List<OfficeEntity> findAll();
    int create(String officeType);
    OfficeEntity findById(Long id) throws ObjectNotFountException;
    void update(String officeType, Long id) throws ObjectNotFountException;
    void deleteById(Long id) throws ObjectNotFountException;
    void deletAll() throws ObjectNotFountException;
    List<OfficeEntity> findByOfficeType(String officeType);
}
