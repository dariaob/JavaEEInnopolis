package repository.impl;

import entity.PatientEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import repository.PatientRepository;
import сonfig.JDBCTemplateConfig;

import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = JDBCTemplateConfig.createNamedParameterJdbcTemplate();
    private static final String FIND_All = "select * from patient";
    private static final String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM patient WHERE id = :id";
    private static final String INSERT_ROW =  "INSERT INTO patient (insurance_id, name, address, card_id) " +
            "VALUES (:insurance_id, :name, :address, :card_id)";
    private static final String DELETE_ROW = "DELETE FROM patient WHERE id = :id";
    private static final RowMapper<PatientEntity> patientRowMapper = (row, rowNum) -> {
        Long patientId = row.getLong("patient_id");
        Long insuranceId = row.getLong("insurance_id");
        String name = row.getString("name");
        String address = row.getString("address");
        Long cardId = row.getLong("card_id");
        return new PatientEntity(patientId, insuranceId, name, address, cardId);
    };
    @Override
    public List<PatientEntity> findAll() {
        return jdbcTemplate.query(FIND_All, patientRowMapper);
    }

    @Override
    public int insertRow(Long insuranceId, String name, String address, Long cardId) {
        // Создание параметров для запроса
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Добавляем параметры в запрос
        params.addValue("insurance_id", insuranceId); // Страховой номер пациента
        params.addValue("name", name); // ФИО пациента
        params.addValue("address", address); // Место жительства
        params.addValue("card_id", cardId); // Номер карты пациента
        return namedParameterJdbcTemplate.update(INSERT_ROW, params); // Возвращает количество вставленных строк
    }

    @Override
    public int deleteRow(Long id) {
        // Проверяем, существует ли запись в таблице doctor
        String checkExistenceQuery = "SELECT COUNT(*) FROM patient WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int count = namedParameterJdbcTemplate.queryForObject(checkExistenceQuery, params, Integer.class);

        if (count == 0) {
            // Если записи нет, то просто выводим информацию, что её нет
            System.out.println("Запись с id = " + id + " в patient не найдена.");
        }

        // Если запись существует, выполняем удаление
        return namedParameterJdbcTemplate.update(DELETE_ROW, params);  // Возвращает количество удаленных строк
    }
}
