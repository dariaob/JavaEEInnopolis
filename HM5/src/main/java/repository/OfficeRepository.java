package repository;

import entity.OfficeEntity;

import java.util.List;

public interface OfficeRepository {
    List<OfficeEntity> findAll();
    int insertRow(Long id, String officeType);
    int deleteRow(Long id);
}
