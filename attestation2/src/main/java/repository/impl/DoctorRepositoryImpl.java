package repository.impl;

import config.JDBCTemplateConfig;
import entity.DoctorEntity;
import entity.PatientCardEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import repository.DoctorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorRepositoryImpl implements DoctorRepository {
    // Создаем экземпляр для подключения к БД
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();

    private static final String FIND_All = "select * from reg_db.doctor";                              // Находим все данные из таблицы reg_db.doctor
    private static final String CREATE = "INSERT INTO reg_db.doctor (\"name\", \"work_hours_from\", \"work_hours_for\", \"office_id\" ) VALUES (?, ?, ?, ?)";  // Добавляем данные в таблицу
    private static final String FIND_BY_ID = "SELECT \"id\", \"name\", \"work_hours_from\", \"work_hours_for\", \"office_id\" FROM reg_db.doctor WHERE \"id\" = ?";              // Находим запись по id
    private static final String UPDATE = "UPDATE reg_db.doctor SET \"name\", \"work_hours_from\", \"work_hours_for\", \"office_id\" = ? WHERE \"id\" = ?";      // Меняет уже существующие данные
    private static final String CHECK_RECEPTION_BY_DOCTOR_ID = "SELECT COUNT(*) FROM reg_db.reception WHERE doctor_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM reg_db.doctor WHERE id = ?";
    private static final String GET_ALL_DOCTOR_IDS = "SELECT id FROM reg_db.doctor";
    private static final String DELETE_RECEPTION_BY_DOCTOR_ID = "DELETE FROM reg_db.reception WHERE doctor_id = ?";
    private static final String FIND_BY_OFFICE = "SELECT \"id\",\"name\", \"work_hours_from\", \"work_hours_for\", \"office_id\" FROM reg_db.doctor WHERE \"office_id\" = ?";


    public static final RowMapper<DoctorEntity> doctorRawMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        String name = row.getString("name");
        LocalDateTime workHoursFrom = row.getTimestamp("work_hours_from").toLocalDateTime();
        LocalDateTime workHoursFor = row.getTimestamp("work_hours_for").toLocalDateTime();
        Long officeId = row.getLong("office_id");
        return new DoctorEntity(id, name, workHoursFrom, workHoursFor, officeId);
    };

    @Override
    public List<DoctorEntity> findAll() {
        return jdbcTemplate.query(FIND_All, doctorRawMapper);
    }

    /**
     *
     * @param name
     * @param workHoursFrom
     * @param workHoursFor
     * @param officeId
     * @return
     * @throws RecordExistsException
     * @throws ObjectNotFountException
     */
    @Override
    public int create(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId) throws RecordExistsException, ObjectNotFountException {
        return jdbcTemplate.update(CREATE, name, workHoursFrom, workHoursFor, officeId);
    }

    @Override
    public DoctorEntity findById(Long id) throws ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }
        try {
            // Применяем Stream для фильтрации
            List<DoctorEntity> doctors = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, doctorRawMapper);

            return doctors.stream()
                    .findFirst()  // В данном случае .findFirst() даст первый элемент или пустой
                    .orElseThrow(() -> new ObjectNotFountException("<Object> с заданным идентификатором не найден."));
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFountException("<Object> с заданным идентификатором не существует.");
        }
    }

    @Override
    public void update(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId, Long id) throws RecordExistsException, ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        // Проверка officeType
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть пустым");
        }

        List<DoctorEntity> doctors = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, doctorRawMapper);
        if (!doctors.isEmpty()) {
            // Если карточка не найдена, обновляем её
            jdbcTemplate.update(UPDATE, name, workHoursFrom, workHoursFor, officeId, id);
            System.out.println("Updated");
        } else {
            System.out.println("Кабинет не найден. Создан новый кабинет.");
            // Если карточка с таким id не найдена, создаем новую
            this.create(name, workHoursFrom, workHoursFor, officeId);
    }
}

    @Override
    public int deleteById(Long id) throws ObjectNotFountException, ImpossibleToDeleteException {
        // Проверяем наличие записей в таблице reception, связанных с данным doctor_id
        int receptionCount = jdbcTemplate.queryForObject(CHECK_RECEPTION_BY_DOCTOR_ID, Integer.class, id);

        if (receptionCount > 0) {
            // Если есть связанные записи, выбрасываем исключение
            throw new ImpossibleToDeleteException("Невозможно удалить врача с id = " + id + ". Есть связанные записи в таблице reception.");
        }

        // Если связанных записей нет, выполняем удаление
        try {
            return jdbcTemplate.update(DELETE_BY_ID, id);
        } catch (DataAccessException e) {
            // Обработка исключений, если такого объекта нет
            throw new ObjectNotFountException("<Object> с заданным идентификатором не существует.");
        }
    }

    @Override
    public int deletAll() {
        List<Long> doctorIds = jdbcTemplate.queryForList(GET_ALL_DOCTOR_IDS, Long.class);
        int record = 0;
        for (Long doctorId : doctorIds) {
            // 2. Удаляем все записи из таблицы reception, связанные с данным врачом
            jdbcTemplate.update(DELETE_RECEPTION_BY_DOCTOR_ID, doctorId);

            // 3. Удаляем запись о враче
            record = jdbcTemplate.update(DELETE_BY_ID, doctorId);
        }
        return record;
    }

    @Override
    public List<DoctorEntity> findByOffice(Long officeId) {
        List<DoctorEntity> doctors = jdbcTemplate.query(FIND_BY_OFFICE, new Object[]{officeId}, doctorRawMapper);

        return doctors.stream()
                .filter(doctor -> doctor.getOfficeId().equals(officeId))
                .collect(Collectors.toList());
    }
}
