package сonfig;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JDBCTemplateConfig {
    private static final String URL = "jdbc:postgresql://localhost:5433/postgres";
    private static final String USER_NAME = "postgres";
    private static final String password = "admin";
    private static final String driverName = "org.postgresql.Driver";
    private static final String schemaName = "homeworkJavaEE";

    /**
     * Настройка для подключения к PostgreSQL
     * @return dataSource
     */
    public static DriverManagerDataSource configureDataSource() {
        var dataSource = new DriverManagerDataSource(URL, USER_NAME, password);
        dataSource.setDriverClassName(driverName);
        dataSource.setSchema(schemaName);
        return dataSource;
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
