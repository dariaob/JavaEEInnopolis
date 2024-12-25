package repository.impl;

import entity.ReceptionEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import repository.ReceptionRepository;
import сonfig.JDBCTemplateConfig;

import java.time.LocalDateTime;
import java.util.List;

public class ReceptionRepositoryImpl implements ReceptionRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate = JDBCTemplateConfig.createNamedParameterJdbcTemplate();
    private static final String FIND_All = "select * from reception";                                                    // Получение всех данных в таблице
    private static final String INSERT_ROW = "INSERT INTO reception (doctor_id, office_id, work_hours_from, work_hours_for, card_id, patient_id, insurance_id) " +           // Вставка данных в таблицу с возвращением сгенерированного id
            "VALUES (:doctor_id, :office_id, :work_hours_from, :work_hours_for, :card_id, :patient_id, :insurance_id) " +
            "RETURNING id";
    private static final String DELETE_ROW = "DELETE FROM reception WHERE id = :id";                                             // Удаление записи
    private static final String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM reception WHERE id = :id";                       // Проверка на существование записи с таким же id
    private static final String CHECK_DUPLICATE_QUERY = "SELECT COUNT(*) FROM reception WHERE doctor_id = :doctor_id " +      //  Проверка на существование записи с такими же параметрами
            "AND office_id = :office_id " +
            "AND work_hours_from = :work_hours_from " +
            "AND work_hours_for = :work_hours_for " +
            "AND card_id = :card_id " +
            "AND patient_id = :patient_id " +
            "AND insurance_id = :insurance_id";
    public static final RowMapper<ReceptionEntity> receptionRawMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        Long doctorId = row.getLong("doctor_id");
        Long officeId = row.getLong("office_id");
        LocalDateTime workHoursFrom = row.getTimestamp("work_hours_from").toLocalDateTime();
        LocalDateTime workHoursFor = row.getTimestamp("work_hours_for").toLocalDateTime();
        Long cardId = row.getLong("card_id");
        Long patientId = row.getLong("patient_id");
        Long insuranceId = row.getLong("insurance_id");
        return new ReceptionEntity(id, doctorId, officeId, workHoursFrom, workHoursFor, cardId, patientId, insuranceId);
    };
    @Override
    public List<ReceptionEntity> findAll() {
        return jdbcTemplate.query(FIND_All, receptionRawMapper);
    }

    /**
     * Добавить прием
     *
     * @param doctorId      Номер (id) врача
     * @param officeId      Номер (id) кабинета
     * @param workHoursFrom Часы работы с
     * @param workHoursFor  Часы работы до
     * @param cardId        (id) Номер кабинета
     * @param patientId     Номер(id) пациента
     * @param insuranceId   Номер(id) полиса страхования
     * @return namedParameterJdbcTemplate Количество вставленных строк
     */
    @Override
    public Long insertRow(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId) {
        // Создание параметров для вставки и проверки дупликатов
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("doctor_id", doctorId); // ID врача
        params.addValue("office_id", officeId); // ID кабинета
        params.addValue("work_hours_from", workHoursFrom); // Время начала приёма
        params.addValue("work_hours_for", workHoursFor); // Время окончания приёма
        params.addValue("card_id", cardId); // Номер карты пациента
        params.addValue("patient_id", patientId); // ID пациента
        params.addValue("insurance_id", insuranceId); // Страховой номер

        // Выполнение запроса для проверки на дубликат
        Integer count = namedParameterJdbcTemplate.queryForObject(CHECK_DUPLICATE_QUERY, params, Integer.class);

        // Если запись уже существует, выбрасываем исключение
        if (count != null && count > 0) {
            throw new IllegalArgumentException("Запись с такими данными уже существует.");
        }

        // Выполнение запроса на вставку с возвратом id
        return namedParameterJdbcTemplate.queryForObject(INSERT_ROW, params, Long.class); // Возвращаем сгенерированный id
    }

    @Override
    public int deleteRow(Long id) {
        // Проверяем, существует ли запись в таблице doctor
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int count = namedParameterJdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, params, Integer.class);

        if (count == 0) {
            // Если записи нет, то просто выводим информацию, что её нет
            System.out.println("Запись с id = " + id + " в reception не найдена.");
        }

        return namedParameterJdbcTemplate.update(DELETE_ROW, params);  // Возвращает количество удаленных строк
    }
}
