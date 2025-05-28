package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.Offices;
import org.dariaob.repositories.OfficesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class OfficesRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OfficesRepository repository;

    private Long activeOfficeId1;
    private Long activeOfficeId2;
    private Long deletedOfficeId;

    @BeforeEach
    public void setUp() {
        Offices office1 = createOffice("Терапевтический кабинет №1", false);
        Offices office2 = createOffice("Хирургический кабинет №2", false);
        Offices office3 = createOffice("Архивный кабинет", true);

        entityManager.persist(office1);
        entityManager.persist(office2);
        entityManager.persist(office3);
        entityManager.flush(); // обязательно перед getId()

        this.activeOfficeId1 = office1.getId();
        this.activeOfficeId2 = office2.getId();
        this.deletedOfficeId = office3.getId();
    }

    private Offices createOffice(String name, boolean isDeleted) {
        Offices office = new Offices();
        office.setName(name);
        office.setDeleted(isDeleted);
        return office;
    }

    @Test
    @DisplayName("Offices - Repository - Find all active test")
    public void findAllActiveTest() {
        List<Offices> offices = repository.findAllActive();

        // Проверяем, что все записи не удалены
        assertThat(offices.stream().allMatch(o -> !o.isDeleted()), is(true));

        // Проверяем, что среди результатов есть кабинеты с нужными именами
        List<String> officeNames = offices.stream().map(Offices::getName).toList();

        // Используем hasItems, чтобы проверить, что хотя бы эти два кабинета есть в списке
        assertThat(officeNames, hasItems("Терапевтический кабинет №1", "Хирургический кабинет №2"));
    }

    @Test
    @DisplayName("Offices - Repository - Find active by id test")
    public void findActiveByIdTest() {
        testFindActiveById(activeOfficeId1, "Терапевтический");
        testFindActiveById(activeOfficeId2, "Хирургический");

        // Проверка для удаленного кабинета
        Optional<Offices> deletedOffice = repository.findActiveById(deletedOfficeId);
        assertThat("Удаленный кабинет не должен находиться", deletedOffice.isPresent(), is(false));
    }

    private void testFindActiveById(Long id, String namePrefix) {
        Optional<Offices> office = repository.findActiveById(id);
        assertThat("Кабинет должен присутствовать", office.isPresent(), is(true));
        assertThat("Название должно начинаться с " + namePrefix,
                office.get().getName(), startsWith(namePrefix));
        assertThat("Кабинет должен быть активным", office.get().isDeleted(), is(false));
    }

    @Test
    @DisplayName("Offices - Repository - Soft delete test")
    @Transactional
    public void softDeleteTest() {
        // 1. Создаем и сохраняем тестовый офис
        Offices office = new Offices();
        office.setName("test");
        office.setDeleted(false);
        entityManager.persist(office);
        entityManager.flush();
        Long idToDelete = office.getId();

        // 2. Мягкое удаление
        repository.softDelete(idToDelete);
        entityManager.flush();
        entityManager.clear();

        // 3. Проверяем флаг deleted
        Offices deletedOffice = entityManager.find(Offices.class, idToDelete);
        assertThat(deletedOffice, is(notNullValue()));
        assertThat(deletedOffice.isDeleted(), is(true));

        // 4. Проверяем, что офис не в списке активных
        List<Offices> activeOffices = repository.findAllActive();
        List<Long> activeIds = activeOffices.stream()
                .map(Offices::getId)
                .toList();

        assertThat(activeIds, not(hasItem(idToDelete)));
    }

    @Test
    @DisplayName("Offices - Repository - Restore test")
    public void restoreTest() {
        // Сначала удаляем
        repository.softDelete(activeOfficeId1);

        // Восстанавливаем
        repository.restore(activeOfficeId1);

        // Проверяем восстановление
        Offices restored = entityManager.find(Offices.class, activeOfficeId1);
        assertThat(restored, notNullValue());
        assertThat(restored.isDeleted(), is(false));

        // Проверяем что кабинет снова в активных
        Optional<Offices> activeOffice = repository.findActiveById(activeOfficeId1);
        assertThat(activeOffice.isPresent(), is(true));
    }

    @Test
    @DisplayName("Offices - Repository - Standard find all includes deleted")
    public void findAllIncludesDeleted() {
        List<Offices> allOffices = repository.findAll();

        // Проверяем что есть все наши кабинеты
        List<Long> ids = allOffices.stream().map(Offices::getId).toList();
        assertThat(ids, hasItems(activeOfficeId1, activeOfficeId2, deletedOfficeId));

        // Проверяем флаги isDeleted
        Map<Long, Boolean> officeDeletionStatus = allOffices.stream()
                .collect(Collectors.toMap(Offices::getId, Offices::isDeleted));

        assertThat(officeDeletionStatus.get(activeOfficeId1), is(false));
        assertThat(officeDeletionStatus.get(activeOfficeId2), is(false));
        assertThat(officeDeletionStatus.get(deletedOfficeId), is(true));
    }
}