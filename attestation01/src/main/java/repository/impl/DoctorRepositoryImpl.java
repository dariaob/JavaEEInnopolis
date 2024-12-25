package repository.impl;

import entity.DoctorEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import repository.DoctorRepository;
import сonfig.JDBCTemplateConfig;

import java.time.LocalDateTime;
import java.util.List;

public class DoctorRepositoryImpl implements DoctorRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = JDBCTemplateConfig.createNamedParameterJdbcTemplate();
    private static final  String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM doctor WHERE id = :id";
    private static final String FIND_All = "select * from doctor";
    private static final String INSERT_ROW = "INSERT INTO doctor (name, work_hours_from, work_hours_for, office_id) " +
            "VALUES (:name, :work_hours_from, :work_hours_for, :office_id)";
    private static final String DELETE_ROW = "DELETE FROM doctor WHERE id = :id";

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

    @Override
    public int insertRow(String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId) {
        // Создание параметров для запроса
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name); // ФИО врача
        params.addValue("office_id", officeId); // ID кабинета
        params.addValue("work_hours_from", workHoursFrom); // Время начала приёма
        params.addValue("work_hours_for", workHoursFor); // Время окончания приёма
        return namedParameterJdbcTemplate.update(INSERT_ROW, params); // Возвращает количество вставленных строк
    }

    @Override
    public int deleteRow(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        // Проверяем, существует ли запись в таблице doctor
        int count = namedParameterJdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, params, Integer.class);

        if (count == 0) {
            // Если записи нет, то просто выводим информацию, что её нет
            throw new IllegalArgumentException("Запись с id = " + id + " в doctor не найдена.");
        }

        return namedParameterJdbcTemplate.update(DELETE_ROW, params);  // Возвращает количество удаленных строк
    }
}

