package repository.impl;

import entity.DoctorEntity;
import entity.ReceptionEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import repository.ReceptionRepository;
import config.JDBCTemplateConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Методы для обработки данных таблицы Прием
 */
public class ReceptionRepositoryImpl implements ReceptionRepository {
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();

    // Константы с запросами
    private static final String FIND_All = "select * from reg_db.reception";                                            // Получить все данные из таблицы
    public static final String CREATE =
            "INSERT INTO reg_db.reception (\"doctor_id\", \"office_id\", \"work_hours_from\", " +
                    "\"work_hours_for\", \"card_id\", \"patient_id\",  \"insurance_id\") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";                                                                     // Создаем новую строку с данными
    private static final String FIND_BY_ID =
            "SELECT \"id\", \"doctor_id\", \"office_id\", \"work_hours_from\", " +
                    "\"work_hours_for\", \"card_id\", \"patient_id\", \"insurance_id\" " +
                    "FROM reg_db.reception WHERE \"id\" = ?";                                                           // Находим запись по id
    private static final String UPDATE =
            "UPDATE reg_db.reception SET \"doctor_id\" = ?, \"office_id\" = ?, \"work_hours_from\" = ?, " +
                    "\"work_hours_for\" = ?, \"card_id\" = ?, \"patient_id\" = ?, \"insurance_id\" = ? WHERE \"id\" = ?";           // Меняет уже существующие данные
    private static final String DELETE_BY_ID =
            "DELETE FROM reg_db.reception WHERE id = ?";                                                                // Удаляем все данные из таблицы
    private static final String GET_ALL_RECEPTION_IDS =
            "SELECT id FROM reg_db.reception";                                                                          // Получаем все id которые есть в таблице
    private static final String CHECK_LINKED_RECORDS_DOCTOR =
            "SELECT COUNT(*) FROM reg_db.reception WHERE doctor_id = ?";                                                // Проверка на связь с doctor

    private static final String CHECK_LINKED_RECORDS_OFFICE =
            "SELECT COUNT(*) FROM reg_db.reception WHERE office_id = ?";                                                // Проверка на связь с office

    private static final String CHECK_LINKED_RECORDS_PATIENT =
            "SELECT COUNT(*) FROM reg_db.reception WHERE patient_id = ?";                                               // Проверка на связь с patient

    private static final String CHECK_LINKED_RECORDS_CARD =
            "SELECT COUNT(*) FROM reg_db.reception WHERE card_id = ?";                                                  // Проверка на связь с patient_card
    private static final String DELETE_ALL = "DELETE FROM reg_db.reception";
    private static final String FIND_BY_DOCTOR_ID = "SELECT \"id\", \"doctor_id\", \"office_id\", \"work_hours_from\", " +
            "\"work_hours_for\", \"card_id\", \"patient_id\", \"insurance_id\" " +
            "FROM reg_db.reception WHERE \"doctor_id\" = ?";


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

    /**
     * @return
     */
    @Override
    public List<ReceptionEntity> findAll() {
        return jdbcTemplate.query(FIND_All, receptionRawMapper);
    }

    /**
     * Создание новой строки данных
     *
     * @param doctorId      ид доктора
     * @param officeId      ид кабинета
     * @param workHoursFrom начало приема
     * @param workHoursFor  конец приема
     * @param cardId        номер карты пациента
     * @param patientId     ид пациента
     * @param insuranceId   номер страхования пациента
     * @return кол-во записанных строк
     */
    @Override
    public int create(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId) {
        return jdbcTemplate.update(CREATE, doctorId, officeId, workHoursFrom, workHoursFor, cardId, patientId, insuranceId);
    }

    /**
     * @param id ид приема
     * @return запись
     * @throws ObjectNotFountException выбрасывает исключение если записи не существует
     */
    @Override
    public ReceptionEntity findById(Long id) throws ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }
        try {
            // Применяем Stream для фильтрации
            List<ReceptionEntity> receptionEntities = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, receptionRawMapper);

            return receptionEntities.stream()
                    .findFirst()  // В данном случае .findFirst() даст первый элемент или пустой
                    .orElseThrow(() -> new ObjectNotFountException("<Object> с заданным идентификатором не найден."));
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFountException("<Object> с заданным идентификатором не существует.");
        }
    }

    /**
     * Обновляет строку с уже существующими данными
     * @param doctorId ид врача
     * @param officeId ид кабинета
     * @param workHoursFrom начало приема
     * @param workHoursFor  конец приема
     * @param cardId номер карточки приема
     * @param patientId ид пациента
     * @param insuranceId номер страховки
     * @param id ид приема
     */
    @Override
    public void update(Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId, Long id) {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        List<ReceptionEntity> receptionEntities = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, receptionRawMapper);
        if (!receptionEntities.isEmpty()) {
            // Если карточка не найдена, обновляем её
            jdbcTemplate.update(UPDATE, doctorId, officeId, workHoursFrom, workHoursFor, cardId, patientId, insuranceId, id);
            System.out.println("Updated");
        } else {
            System.out.println("Прием не найден. Создан новый прием.");
            // Если карточка с таким id не найдена, создаем новую
            this.create(doctorId, officeId, workHoursFrom, workHoursFor, cardId, patientId, insuranceId);
        }
    }

    /**
     * @param id id приема
     * @param doctorId ид врача
     * @param officeId ид кабинета
     * @param patientId ид пациента
     * @param cardId ид карточки пациента
     * @return кол-во измененных строк
     */
    @Override
    public int deleteById(Long id, Long doctorId, Long officeId, Long patientId, Long cardId) throws ObjectNotFountException, ImpossibleToDeleteException {
        int doctorCount = jdbcTemplate.queryForObject(CHECK_LINKED_RECORDS_DOCTOR, Integer.class, doctorId);
        int officeCount = jdbcTemplate.queryForObject(CHECK_LINKED_RECORDS_OFFICE, Integer.class, officeId);
        int patientCount = jdbcTemplate.queryForObject(CHECK_LINKED_RECORDS_PATIENT, Integer.class, patientId);
        int cardCount = jdbcTemplate.queryForObject(CHECK_LINKED_RECORDS_CARD, Integer.class, cardId);

        // Если есть связанные записи, выбрасываем исключение
        if (doctorCount > 0 || officeCount > 0 || patientCount > 0 || cardCount > 0) {
            throw new ImpossibleToDeleteException("Невозможно удалить запись. Есть связанные записи в других таблицах.");
        }

        // Если связанных записей нет, выполняем удаление
        try {
            return jdbcTemplate.update(DELETE_BY_ID, id);
        } catch (DataAccessException e) {
            // Обработка исключений, если такого объекта нет
            throw new ObjectNotFountException("<Object> с заданным идентификатором не существует.");
        }
    }

    /**
     * Удаляет все записи из таблицы
     * @return
     */
    @Override
    public int deletAll() throws ObjectNotFountException, ImpossibleToDeleteException {
        List<Long> receptionIds = jdbcTemplate.queryForList(GET_ALL_RECEPTION_IDS, Long.class);
        if (receptionIds.size() > 0) {
               return jdbcTemplate.update(DELETE_ALL);
            } else {
                // Если таблица пуста, выбрасываем исключение
                throw new ObjectNotFountException("В таблице нет записей");
            }
        }


    /**
     * @param doctorId
     * @return
     */
    @Override
    public List<ReceptionEntity> findByDoctorId(Long doctorId) {
        List<ReceptionEntity> receptionEntities = jdbcTemplate.query(FIND_BY_DOCTOR_ID, new Object[]{doctorId}, receptionRawMapper);

        return receptionEntities.stream()
                .filter(entity -> entity.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }
}
