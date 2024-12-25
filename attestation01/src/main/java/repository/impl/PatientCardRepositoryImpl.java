package repository.impl;

import entity.OfficeEntity;
import entity.PatientCardEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import repository.PatientCardRepository;
import сonfig.JDBCTemplateConfig;

import java.util.List;

public class PatientCardRepositoryImpl implements PatientCardRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = JDBCTemplateConfig.createNamedParameterJdbcTemplate();
    public static final String FIND_All = "select * from patient_card";
    private static final  String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM patient_card WHERE id = :id";
    private static final String INSERT_ROW =  "INSERT INTO patient_card (id, symptoms, diagnosis, medicine) " +
            "VALUES (:id, :symptoms, :diagnosis, :medicine)";
    private static final String DELETE_ROW = "DELETE FROM patient_card WHERE id = :id";

    public static final RowMapper<PatientCardEntity> patientCardRowMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        String symptoms = row.getString("symptoms");
        String diagnosis = row.getString("diagnosis");
        String medicine = row.getString("medicine");
        return new PatientCardEntity(id, symptoms, diagnosis, medicine);
    };

    @Override
    public List<PatientCardEntity> findAll() {
        return jdbcTemplate.query(FIND_All, patientCardRowMapper);
    }

    @Override
    public void insertRow(Long patientCardId, String symptoms, String diagnosis, String medicine) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", patientCardId);

        // Проверим, существует ли уже карта пациента с таким id
        int count = namedParameterJdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, params, Integer.class);

        if (count > 0) {
            // Если запись существует, сообщаем об этом
            System.out.println("Карта пациента с id " + patientCardId + " уже существует.");
        } else {
            // Если записи нет, вставляем новую
            params.addValue("symptoms", symptoms);
            params.addValue("diagnosis", diagnosis);
            params.addValue("medicine", medicine);

           namedParameterJdbcTemplate.update(INSERT_ROW, params);
        }
    }

    @Override
    public int deleteRow(Long id) {
        // Проверяем, существует ли запись в таблице doctor
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int count = namedParameterJdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, params, Integer.class);

        if (count == 0) {
            // Если записи нет, то просто выводим информацию, что её нет
            throw new IllegalArgumentException("Запись с id = " + id + " в patient_card не найдена.");
        }

        return namedParameterJdbcTemplate.update(DELETE_ROW, params);  // Возвращает количество удаленных строк
    }
}
