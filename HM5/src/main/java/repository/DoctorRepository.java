package repository;

import entity.DoctorEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorRepository {
    List<DoctorEntity> findAll();
    int insertRow(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId);
    int deleteRow(Long id);
}
