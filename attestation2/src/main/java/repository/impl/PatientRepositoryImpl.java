package repository.impl;

import config.JDBCTemplateConfig;
import entity.DoctorEntity;
import entity.PatientCardEntity;
import entity.PatientEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import repository.PatientRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация методов для получения данных пациента
 */
public class PatientRepositoryImpl implements PatientRepository {
    // Создание экземпляра jdbcTemplate для обращения к БД
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();

    private static final String FIND_All = "select * from reg_db.patient";                     // Получение всех данных
    private static final String CREATE = "INSERT INTO reg_db.patient (\"insurance_id\", \"name\", \"address\",\"card_id\" ) VALUES (?, ?, ?, ?)";  // Добавляем данные в таблицу
    private static final String FIND_BY_ID = "SELECT \"patient_id\", \"insurance_id\", \"name\", \"address\",\"card_id\"  FROM reg_db.patient WHERE \"patient_id\" = ?";
    private static final String FIND_BY_INSURANCE_ID = "SELECT \"patient_id\", \"insurance_id\", \"name\", \"address\", \"card_id\"  FROM reg_db.patient WHERE \"insurance_id\" = ?"; // Находим запись по id
    private static final String UPDATE = "UPDATE reg_db.patient SET \"insurance_id\" = ?, \"name\" = ?, \"address\" = ?, \"card_id\" = ?  WHERE \"patient_id\" = ?";      // Меняет уже существующие данные
    private static final String DELETE_BY_ID = "DELETE FROM reg_db.patient WHERE patient_id = ?";
    private static final String GET_ALL_PATIENT_CARD_IDS = "SELECT card_id FROM reg_db.patient";
    private static final String GET_ALL_PATIENT_IDS = "SELECT patient_id FROM reg_db.patient";
    private static final String DELETE_RECEPTION_BY_PATIENT_ID =  "DELETE FROM reg_db.reception WHERE patient_id = ?";
    private static final String DELETE_PATIENT_CARD = "SELECT COUNT(*) FROM reg_db.patient_card WHERE card_id = ?;";
    private static final String DELETE_ALL = "DELETE FROM reg_db.patient";
    private static final String CHECK_LINKED_RECORDS = "SELECT COUNT(*) FROM reg_db.reception WHERE patient_id = ? UNION ALL " +
                                                                        "SELECT COUNT(*) FROM reg_db.patient_card WHERE card_id = ?"; // Проверка связанных записей

    /**
     * Создание маппера для пациента
     */
    private static final RowMapper<PatientEntity> patientRowMapper = (row, rowNum) -> {
        Long patientId = row.getLong("patient_id");
        Long insuranceId = row.getLong("insurance_id");
        String name = row.getString("name");
        String address = row.getString("address");
        Long cardId = row.getLong("card_id");
        return new PatientEntity(patientId, insuranceId, name, address, cardId);
    };

    /**
     * Возвращает все данные из таблицы Пациент
     * @return 
     */
    @Override
    public List<PatientEntity> findAll() {
        return jdbcTemplate.query(FIND_All, patientRowMapper);
    }

    /**
     * Создает новый экземпляр таблицы Пациент
     * @param insuranceId номер страховки
     * @param name фио пациента
     * @param address адрес
     * @param cardId номер карты пациента
     * @return кол-во созданных строк
     */
    @Override
    public int create(Long insuranceId, String name, String address, Long cardId) throws ObjectNotFountException {
        return jdbcTemplate.update(CREATE, insuranceId, name, address, cardId);
    }

    /**
     * Получить экземпляр таблицы по номеру id
     * @param patientId id пациента
     * @return запись из таблицы Пациент
     */
    @Override
    public PatientEntity findById(Long patientId) throws ObjectNotFountException {
        // Проверка id
        if (patientId < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }
        try {
            // Применяем Stream для фильтрации
            List<PatientEntity> patientEntities = jdbcTemplate.query(FIND_BY_ID, new Object[]{patientId}, patientRowMapper);

            return patientEntities.stream()
                    .findFirst()  // В данном случае .findFirst() даст первый элемент или пустой
                    .orElseThrow(() -> new ObjectNotFountException("<Object> с заданным идентификатором не найден."));
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFountException("<Object> с заданным идентификатором не существует.");
        }
    }

    /**
     * @param insuranceId номер страховки
     * @param name фио пациента
     * @param address адрес
     * @param cardId номер карты пациента
     * @param patientId id пациента
     */
    @Override
    public void update(Long insuranceId, String name, String address, Long cardId, Long patientId) throws ObjectNotFountException, RecordExistsException {
        // Проверка id
        if (patientId < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        List<PatientEntity> patientEntities = jdbcTemplate.query(FIND_BY_ID, new Object[]{patientId}, patientRowMapper);
        if (!patientEntities.isEmpty()) {
            // Если карточка не найдена, обновляем её
            jdbcTemplate.update(UPDATE, insuranceId, name, address, cardId, patientId);
            System.out.println("Updated");
        } else {
            System.out.println("Кабинет не найден. Создан новый кабинет.");
            // Если карточка с таким id не найдена, создаем новую
            this.create(insuranceId, name, address, cardId);
        }
    }

    /**
     * Удаление записи по id
     * @param patientId id пациента
     * @return кол-во удаленных записей
     */
    @Override
    public int deleteById(Long patientId) throws ObjectNotFountException, ImpossibleToDeleteException {
        // Проверка наличия связанных записей в таблицах reception и patient_card
        Integer count = jdbcTemplate.queryForObject(CHECK_LINKED_RECORDS, Integer.class, patientId, patientId);

        if (count != null && count > 0) {
            // Если есть связанные записи, выбрасываем исключение
            throw new ImpossibleToDeleteException("Невозможно удалить пациента с id " + patientId + ". Есть связанные записи в других таблицах.");
        }

        try {
            // Если нет связанных записей, удаляем пациента
            return jdbcTemplate.update(DELETE_BY_ID, patientId);
        } catch (DataAccessException e) {
            // В случае ошибки при удалении, выводим сообщение
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * @return 
     */
    @Override
    public int deletAll() throws ObjectNotFountException {
        System.out.println("Будут удалены все связанные записи из связанных таблиц");
        int record = 0;
        // 1. Получаем список всех id пациентов
        List<Long> patientCardIds = jdbcTemplate.queryForList(GET_ALL_PATIENT_CARD_IDS, Long.class);
        List<Long> patientIds = jdbcTemplate.queryForList(GET_ALL_PATIENT_IDS, Long.class);
        if (!patientCardIds.isEmpty()) {
            for (Long cardId : patientCardIds) {
                // 2. Удаляем все записи из таблицы patient_card
                jdbcTemplate.update(DELETE_PATIENT_CARD, cardId);
            }
        }
        if (!patientIds.isEmpty()) {
            for (Long patientId : patientIds) {
                jdbcTemplate.update(DELETE_RECEPTION_BY_PATIENT_ID, patientId);
            }
        }
        // Выполняем удаление
        try {
            return jdbcTemplate.update(DELETE_ALL);
        } catch (DataAccessException e) {
            // Обработка исключений, если что-то пошло не так при удалении
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * @param insuranceId 
     * @return
     */
    @Override
    public List<PatientEntity> findByDInsuranceId(Long insuranceId) {
        List<PatientEntity> patientEntities = jdbcTemplate.query(FIND_BY_INSURANCE_ID, new Object[]{insuranceId}, patientRowMapper);
        // Фильтруем по симптомам
        return patientEntities.stream()
                .filter(card -> card.getInsuranceId().equals(insuranceId))
                .collect(Collectors.toList());
    }
}
