package org.dariaob;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;

/**
 * The type Test with container.
 */
public abstract class TestWithContainer {

    /**
     * The constant DB_URL.
     */
    protected static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;";
    /**
     * The constant DB_USERNAME.
     */
    protected static final String DB_USERNAME = "sa";
    /**
     * The constant DB_PASSWORD.
     */
    protected static final String DB_PASSWORD = "";

    /**
     * The constant DATA_SOURCE.
     */
    protected static DataSource DATA_SOURCE;
    /**
     * The constant FLYWAY.
     */
    protected static Flyway FLYWAY;

    static {
        DATA_SOURCE = new DriverManagerDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);

        FLYWAY = Flyway.configure()
                .cleanDisabled(false)
                .locations("classpath:db/migration-test")
                .dataSource(DATA_SOURCE)
                .load();

        // Очистка и миграция схемы
        FLYWAY.clean();
        FLYWAY.migrate();
    }

    /**
     * Configure data base source.
     *
     * @param registry the registry
     */
    @DynamicPropertySource
    protected static void configureDataBaseSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> DB_URL);
        registry.add("spring.datasource.username", () -> DB_USERNAME);
        registry.add("spring.datasource.password", () -> DB_PASSWORD);
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }
}
