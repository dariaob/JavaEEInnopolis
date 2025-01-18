package repository.impl;

import config.JDBCTemplateConfig;
import entity.PatientCardEntity;
import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.PatientCardRepository;
import org.springframework.jdbc.core.RowMapper;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Карточка пациента
 */
public class PatientCardRepositoryImpl implements PatientCardRepository {
    // Создаем экземпляр для подключения к БД
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();

    private static final String CHECK_RECEPTION_BY_CARD_ID = "SELECT COUNT(*) FROM reg_db.reception WHERE card_id = ?";    // SQL-запрос для проверки наличия записей в таблице reception, которые ссылаются на patient_card
    private static final String CHECK_PATIENT_BY_CARD_ID = "SELECT COUNT(*) FROM reg_db.patient WHERE card_id = ?";        // SQL-запрос для проверки наличия записей в таблице patient, которые ссылаются на patient_card
    private static final String FIND_All = "select * from reg_db.patient_card";                              // Находим все данные из таблицы office
    private static final String CREATE = "INSERT INTO reg_db.patient_card (\"symptoms\", \"diagnosis\", \"medicine\") VALUES (?, ?, ?)";  // Добавляем данные в таблицу
    private static final String FIND_BY_ID = "SELECT \"id\", \"symptoms\", \"diagnosis\", \"medicine\" FROM reg_db.patient_card WHERE \"id\" = ?";              // Находим запись по id
    private static final String UPDATE = "UPDATE reg_db.patient_card SET \"symptoms\" = ?, \"diagnosis\" = ?, \"medicine\" = ? WHERE \"id\" = ?";;      // Меняет уже существующие данные
    private static final String DELETE_BY_ID = "DELETE FROM reg_db.patient_card WHERE id = ?";
    private static final String GET_ALL_PATIENT_CARD_IDS = "SELECT id FROM reg_db.patient_card";
    private static final String DELETE_RECEPTION_BY_CARD_ID =  "DELETE FROM reg_db.reception WHERE card_id = ?";
    private static final String FIND_BY_DIAGNOSIS = "SELECT \"id\", \"symptoms\", \"diagnosis\", \"medicine\" FROM reg_db.patient_card WHERE \"diagnosis\" = ?";

    public static final RowMapper<PatientCardEntity> patientCardRowMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        String symptoms = row.getString("symptoms");
        String diagnosis = row.getString("diagnosis");
        String medicine = row.getString("medicine");
        return new PatientCardEntity(id, symptoms, diagnosis, medicine);
    };

    /**
     * Выводим все записи из таблицы
     *
     * @return все записи
     */
    @Override
    public List<PatientCardEntity> findAll() {
        return jdbcTemplate.query(FIND_All, patientCardRowMapper);
    }

    /**
     * Создаем новую запись в таблице
     * @param symptoms симптомы
     * @param diagnosis диагноз
     * @param medicine  лекарства
     * @return кол-во записей
     */
    @Override
    public int create(String symptoms, String diagnosis, String medicine)  {
        return jdbcTemplate.update(CREATE, symptoms, diagnosis, medicine);
    }

    /**
     * Находит запись по id
     * @param id записи
     * @return запись
     */
    @Override
    public PatientCardEntity findById(Long id) throws ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }
        try {
            // Применяем Stream для фильтрации или других операций
            List<PatientCardEntity> cards = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, patientCardRowMapper);

            return cards.stream()
                    .findFirst()  // В данном случае .findFirst() даст первый элемент или пустой
                    .orElseThrow(() -> new ObjectNotFountException("<Object>с заданным идентификатором не найден."));
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * Обновление существующей записи
     * @param symptoms симптомы
     * @param diagnosis диагноз
     * @param medicine лекарства
     * @param id ID
     */
    @Override
    public void update(String symptoms, String diagnosis, String medicine, Long id) {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        // Проверка officeType
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Диагноз не может быть пустым");
        }

        List<PatientCardEntity> cards = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, patientCardRowMapper);
        if (!cards.isEmpty()) {
            // Если карточка не найдена, обновляем её
            jdbcTemplate.update(UPDATE, symptoms, diagnosis, medicine, id);
            System.out.println("Updated");
        } else {
            System.out.println("Карточка пациента не найдена. Создана новая карточка.");
            // Если карточка с таким id не найдена, создаем новую
            this.create(symptoms, diagnosis, medicine);
    }
    }

    /**
     * Удаляет запись по id
     * @param id
     * @return запись
     */
    @Override
    public int deleteById(Long id) throws ObjectNotFountException, ImpossibleToDeleteException {
        // Проверяем наличие связанных записей в таблице reception
        int receptionCount = jdbcTemplate.queryForObject(CHECK_RECEPTION_BY_CARD_ID, Integer.class, id);
        if (receptionCount > 0) {
            throw new ImpossibleToDeleteException("Невозможно удалить карту пациента с id = " + id + ". Есть связанные записи в таблице reception.");
        }

        // Проверяем наличие связанных записей в таблице patient
        int patientCount = jdbcTemplate.queryForObject(CHECK_PATIENT_BY_CARD_ID, Integer.class, id);
        if (patientCount > 0) {
            throw new ImpossibleToDeleteException("Невозможно удалить карту пациента с id = " + id + ". Есть связанные записи в таблице patient.");
        }

        // Если связанных записей нет, выполняем удаление
        try {
            return jdbcTemplate.update(DELETE_BY_ID, id);
        } catch (DataAccessException e) {
            // Обработка исключений, если что-то пошло не так при удалении
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * Метод для удаления всех записей из таблицы patient_card с удалением связанных записей.
     *
     */
    @Override
    public int deletAll() {
        System.out.println("Будут удалены все связанные записи из связанных таблиц");
        int record = 0;
        // 1. Получаем список всех id карт пациента
        List<Long> patientCardIds = jdbcTemplate.queryForList(GET_ALL_PATIENT_CARD_IDS, Long.class);

        for (Long cardId : patientCardIds) {
            // 2. Удаляем все записи из таблицы reception, связанные с данной картой пациента
            jdbcTemplate.update(DELETE_RECEPTION_BY_CARD_ID, cardId);

            // 3. Удаляем запись о карте пациента
            record = jdbcTemplate.update(DELETE_BY_ID, cardId);
        }
        return record;
    }

    /**
     * Находит все записи с похожим симптомами
     * @param diagnosis диагноз
     * @return список записей
     */
    @Override
    public List<PatientCardEntity> findByDiagnosis(String diagnosis) {
        List<PatientCardEntity> cards = jdbcTemplate.query(FIND_BY_DIAGNOSIS, new Object[]{diagnosis}, patientCardRowMapper);
        // Фильтруем по симптомам
        return cards.stream()
                .filter(card -> card.getDiagnosis().equalsIgnoreCase(diagnosis))
                .collect(Collectors.toList());
    }
 }

