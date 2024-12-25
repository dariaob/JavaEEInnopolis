package repository.impl;

import entity.OfficeEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import repository.OfficeRepository;
import сonfig.JDBCTemplateConfig;

import java.util.List;

public class OfficeRepositoryImpl implements OfficeRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = JDBCTemplateConfig.createNamedParameterJdbcTemplate();
    private static final  String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM office WHERE id = :id";
    private static final String CHECK_DOCTOR_QUERY = "SELECT COUNT(*) FROM doctor WHERE office_id = :id";
    private static final String CHECK_RECEPTION_QUERY = "SELECT COUNT(*) FROM reception WHERE office_id = :id";
    private static final String DELETE_DOCTOR_QUERY = "DELETE FROM doctor WHERE office_id = :id";
    private static final String DELETE_RECEPTION_QUERY = "DELETE FROM reception WHERE office_id = :id";
    private static final String FIND_All = "select * from office";
    private static final String INSERT_ROW = "INSERT INTO office (id, office_type) " +
            "VALUES (:id, :office_type)";
    private static final String DELETE_ROW = "DELETE FROM office WHERE id = :id";

    private static final RowMapper<OfficeEntity> officeRawMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        String officeType = row.getString("office_type");
        return new OfficeEntity(id, officeType);
    };
    @Override
    public List<OfficeEntity> findAll() {
        return jdbcTemplate.query(FIND_All, officeRawMapper);
    }

    @Override
    public int insertRow(Long id, String officeType) {
        // Создание параметров для запроса
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id); // ID кабинета
        params.addValue("office_type", officeType); // Тип кабинета
        return namedParameterJdbcTemplate.update(INSERT_ROW, params); // Возвращает количество вставленных строк
    }

    @Override
    public int deleteRow(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        // Проверяем, существует ли запись в таблице office
        int count = namedParameterJdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, params, Integer.class);
        // Проверяем, существует ли запись в таблице doctor
        int doctorCount = namedParameterJdbcTemplate.queryForObject(CHECK_DOCTOR_QUERY, params, Integer.class);
        // Проверяем, существует ли запись в таблице reception
        int receptionCount = namedParameterJdbcTemplate.queryForObject(CHECK_RECEPTION_QUERY, params, Integer.class);
        if (doctorCount > 0 || receptionCount > 0) {
            // Если на office_id есть ссылки, выбрасываем исключение
            throw new IllegalStateException("Невозможно удалить office, так как на него ссылаются записи в таблицах doctor или reception.");
        }

        if (count == 0) {
            // Если записи нет, то просто выводим информацию, что её нет
            throw new IllegalArgumentException("Запись с id = " + id + " в office не найдена.");
        }

        // Если запись существует, выполняем удаление
        return namedParameterJdbcTemplate.update(DELETE_ROW, params);  // Возвращает количество удаленных строк
    }
}
