package config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JDBCTemplateConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/registration_db";
    private static final String USER_NAME = "postgres";
    private static final String password = "postgres";
    private static final String driverName = "org.postgresql.Driver";
    private static final String schemaName = "reg_db";

    /**
     * Настройка для подключения к PostgreSQL
     * @return dataSource
     */
    public static DriverManagerDataSource configureDataSource() {
        try {
            var dataSource = new DriverManagerDataSource(URL, USER_NAME, password);
            dataSource.setDriverClassName(driverName);
            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при настройке DataSource", e);
        }
    }

    /**
     * Создает JdbcTemplate с заполненными данными для подключения
     * @return JdbcTemplate
     */
    public static JdbcTemplate createJdbcTemplate() {
        var driver = configureDataSource();
        return new JdbcTemplate(driver);
    }

    /**
     * Создает NamedParameterJdbcTemplate
     * с указанными данными для подключения
     * @return NamedParameterJdbcTemplate (т.е. JdbcTemplate с параметрами)
     */
    public static NamedParameterJdbcTemplate createNamedParameterJdbcTemplate() {
        var driver = configureDataSource();
        return new NamedParameterJdbcTemplate(driver);
    }
}
