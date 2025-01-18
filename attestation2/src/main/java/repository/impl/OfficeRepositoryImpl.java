package repository.impl;

import config.JDBCTemplateConfig;
import entity.OfficeEntity;
import exceptions.ObjectNotFountException;
import exceptions.RecordExistsException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import repository.OfficeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Кабинет врача
 */
public class OfficeRepositoryImpl implements OfficeRepository {
    // Создаем экземпляр для подключения к БД
    private final JdbcTemplate jdbcTemplate = JDBCTemplateConfig.createJdbcTemplate();

    private static final String FIND_All = "select * from office";                              // Находим все данные из таблицы office
    private static final String CREATE = "INSERT INTO reg_db.office (\"office_type\") VALUES (?)";  // Добавляем данные в таблицу
    private static final String FIND_BY_ID = "SELECT \"id\", \"office_type\" FROM reg_db.office WHERE \"id\" = ?";              // Находим запись по id
    private static final String CHECK_EXISTENCE_QUERY = "SELECT COUNT(*) FROM reg_db.office WHERE \"id\" = ?";
    private static final String CHECK_DOCTOR_QUERY = "SELECT COUNT(*) FROM reg_db.doctor WHERE \"office_id\" = ?";
    private static final String CHECK_RECEPTION_QUERY = "SELECT COUNT(*) FROM reg_db.reception WHERE \"office_id\" = ?";         // SQL для проверки наличия записи с таким id
    private static final String UPDATE = "UPDATE reg_db.office SET \"office_type\" = ? WHERE \"id\" = ?";      // Меняет уже существующие данные
    private static final String DELETE_BY_ID = "DELETE FROM office WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM office";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM office";
    private static final String FIND_BY_OFFICE_TYPE = "SELECT \"id\", \"office_type\" FROM reg_db.office WHERE \"office_type\" = ?";

    /**
     * Создаем новый объект Java
     */
    private static final RowMapper<OfficeEntity> officeRawMapper = (row, rowNum) -> {
        Long id = row.getLong("id");
        String officeType = row.getString("office_type");
        return new OfficeEntity(id, officeType);
    };

    /**
     * Выводим все записи из таблицы
     *
     * @return все записи
     */
    @Override
    public List<OfficeEntity> findAll() {
        return jdbcTemplate.query(FIND_All, officeRawMapper);
    }

    /**
     * Создаем новый экземпляр таблицы
     *
     * @param officeType тип Офиса
     * @return
     */
    @Override
    public int create(String officeType) throws RecordExistsException, ObjectNotFountException {
        return jdbcTemplate.update(CREATE, officeType);
    }

    /**
     * Находим запись из таблицы по id
     *
     * @param id id Кабинета
     * @return
     */
    @Override
    public OfficeEntity findById(Long id) throws ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }
        try {
            // Применяем Stream для фильтрации или других операций
            List<OfficeEntity> offices = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, officeRawMapper);

            // Применяем Stream для обработки, хотя это не обязательно для одного объекта
            return offices.stream()
                    .findFirst()  // В данном случае .findFirst() даст первый элемент или пустой
                    .orElseThrow(() -> new ObjectNotFountException("<Object>с заданным идентификатором не найден."));
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * Обновление полей существующего объекта базы данных по id.
     * При отсутствии объекта с искомым id, формируется сообщение об отсутствии данных об объекте и создается новый объект.
     * При ошибке в требованиях к полям класса выбрасывается исключение.
     *
     * @param id         id кабинета
     * @param officeType название кабинета
     */
    @Override
    public void update(String officeType, Long id) throws IllegalArgumentException, RecordExistsException, ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        // Проверка officeType
        if (officeType == null || officeType.trim().isEmpty()) {
            throw new IllegalArgumentException("OfficeType не может быть пустым");
        }

        List<OfficeEntity> offices = jdbcTemplate.query(FIND_BY_ID, new Object[]{id}, officeRawMapper);
        if (!offices.isEmpty()) {
            // Если офис найден, обновляем его
            jdbcTemplate.update(UPDATE, officeType, id);
            System.out.println("Updated");
        } else {
            System.out.println("Кабинет не найден. Создан новый кабинет.");
            // Если офис с таким id не найден, создаем новый
            this.create(officeType);

        }
    }

    /**
     * Удаляет запись по id
     *
     * @param id id кабинета
     */
    @Override
    public void deleteById(Long id) throws ObjectNotFountException {
        // Проверка id
        if (id < 0) {
            throw new IllegalArgumentException("ID не должно быть отрицательным числом");
        }

        Integer count = this.checkIfExists(id);

        // Проверяем, существует ли запись в таблице doctor
        Integer doctorCount = jdbcTemplate.queryForObject(CHECK_DOCTOR_QUERY, Integer.class, id);
        // Проверяем, существует ли запись в таблице reception
        Integer receptionCount = jdbcTemplate.queryForObject(CHECK_RECEPTION_QUERY, Integer.class, id);
        System.out.println(doctorCount + " , " + receptionCount);
        if ((doctorCount != null && doctorCount > 0) || (receptionCount != null  && receptionCount > 0)) {
            // Если на office_id есть ссылки, выбрасываем исключение
            throw new IllegalStateException("Невозможно удалить office, так как на него ссылаются записи в таблицах doctor или reception.");
        }

        if (count != null && count > 0) {
            jdbcTemplate.update(DELETE_BY_ID, id);
        } else {
            throw new ObjectNotFountException("<Object>с заданным идентификатором не существует.");
        }
    }

    /**
     * Удаляет все строки в таблице
     */
    @Override
    public void deletAll() throws ObjectNotFountException {
        // Проверка, есть ли записи в таблице
        Integer count = jdbcTemplate.queryForObject(COUNT_ALL, Integer.class);

        if (count != null && count > 0) {
            // Если записи есть, удаляем их
            jdbcTemplate.update(DELETE_ALL);
        } else {
            // Если таблица пуста, выбрасываем исключение
            throw new ObjectNotFountException("В таблице нет записей");
        }
    }

    /**
     * Проверка, существует ли уже запись с таким id
     *
     * @param id id офиса
     * @return количество записей
     */
    public Integer checkIfExists(Long id) {
        return jdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY, Integer.class, id);
    }

    /**
     * Находит все кабинеты по типу
     * @param officeType тип кабинета
     */
    public List<OfficeEntity> findByOfficeType(String officeType) {
        List<OfficeEntity> offices = jdbcTemplate.query(FIND_BY_OFFICE_TYPE, new Object[]{officeType}, officeRawMapper);
        // Применяем Stream для фильтрации или других операций
        return offices.stream()
                .filter(office -> office.getOfficeType().equalsIgnoreCase(officeType))
                .collect(Collectors.toList());
    }
}
