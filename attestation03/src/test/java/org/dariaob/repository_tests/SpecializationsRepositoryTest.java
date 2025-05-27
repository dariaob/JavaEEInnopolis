package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.Specializations;
import org.dariaob.repositories.SpecializationsRepository;
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

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class SpecializationsRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpecializationsRepository repository;

    private Long activeSpecId1;
    private Long activeSpecId2;
    private Long deletedSpecId;

    @BeforeEach
    public void setUp() {
        Specializations spec1 = createSpecialization("Терапевт", "Врач общей практики", false);
        Specializations spec2 = createSpecialization("Хирург", "Оперативное лечение", false);
        Specializations spec3 = createSpecialization("Архивная специализация", "Не используется", true);

        entityManager.persist(spec1);
        entityManager.persist(spec2);
        entityManager.persist(spec3);
        entityManager.flush();

        this.activeSpecId1 = spec1.getId();
        this.activeSpecId2 = spec2.getId();
        this.deletedSpecId = spec3.getId();
    }

    private Specializations createSpecialization(String name, String description, boolean isDeleted) {
        Specializations spec = new Specializations();
        spec.setName(name);
        spec.setDescription(description);
        spec.setDeleted(isDeleted);
        return spec;
    }

    @Test
    @DisplayName("Specializations - Repository - Find all active test")
    public void findAllActiveTest() {
        List<Specializations> specializations = repository.findAllActive();

        assertThat(specializations.stream().allMatch(s -> !s.isDeleted()), is(true));
        assertThat(specializations.stream().map(Specializations::getName).toList(),
                hasItems("Терапевт", "Хирург"));
    }

    @Test
    @DisplayName("Specializations - Repository - Find active by id test")
    public void findActiveByIdTest() {
        testFindActiveById(activeSpecId1, "Терапевт");
        testFindActiveById(activeSpecId2, "Хирург");

        Optional<Specializations> deletedSpec = repository.findActiveById(deletedSpecId);
        assertThat("Удаленная специализация не должна находиться",
                deletedSpec.isPresent(), is(false));
    }

    private void testFindActiveById(Long id, String expectedName) {
        Optional<Specializations> spec = repository.findActiveById(id);
        assertThat("Специализация должна присутствовать", spec.isPresent(), is(true));
        assertThat(spec.get().getName(), equalTo(expectedName));
        assertThat("Специализация должна быть активной", spec.get().isDeleted(), is(false));
    }

    @Test
    @DisplayName("Specializations - Repository - Soft delete test")
    @Transactional
    public void softDeleteTest() {
        repository.softDelete(activeSpecId1);
        entityManager.flush();
        entityManager.clear();

        Specializations deletedSpec = entityManager.find(Specializations.class, activeSpecId1);
        assertThat(deletedSpec, notNullValue());
        assertThat(deletedSpec.isDeleted(), is(true));

        List<Specializations> activeSpecs = repository.findAllActive();
        assertThat(activeSpecs.stream().map(Specializations::getId).toList(),
                not(hasItem(activeSpecId1)));
        assertThat(activeSpecs.stream().map(Specializations::getId).toList(),
                hasItem(activeSpecId2));
    }

    @Test
    @DisplayName("Specializations - Repository - Restore test")
    @Transactional
    public void restoreTest() {
        repository.softDelete(activeSpecId1);
        repository.restore(activeSpecId1);
        entityManager.flush();
        entityManager.clear();

        Specializations restored = entityManager.find(Specializations.class, activeSpecId1);
        assertThat(restored, notNullValue());
        assertThat(restored.isDeleted(), is(false));

        Optional<Specializations> activeSpec = repository.findActiveById(activeSpecId1);
        assertThat(activeSpec.isPresent(), is(true));
    }

    @Test
    @DisplayName("Specializations - Repository - Standard find all includes deleted")
    public void findAllIncludesDeleted() {
        List<Specializations> allSpecs = repository.findAll();

        assertThat(allSpecs.stream().map(Specializations::getId).toList(),
                hasItems(activeSpecId1, activeSpecId2, deletedSpecId));

        Map<Long, Boolean> specDeletionStatus = allSpecs.stream()
                .collect(Collectors.toMap(Specializations::getId, Specializations::isDeleted));

        assertThat(specDeletionStatus.get(activeSpecId1), is(false));
        assertThat(specDeletionStatus.get(activeSpecId2), is(false));
        assertThat(specDeletionStatus.get(deletedSpecId), is(true));
    }

    @Test
    @DisplayName("Specializations - Repository - Find by name ignore case")
    public void findByNameIgnoreCaseTest() {
        Optional<Specializations> spec = repository.findFirstByNameIgnoreCaseAndIsDeletedFalseOrderByIdDesc("терапевт");
        System.out.println(activeSpecId1);
        assertThat(spec.isPresent(), is(true));
        assertThat(spec.get().getId(), equalTo(activeSpecId1));
    }

    @Test
    @DisplayName("Specializations - Repository - Search by name part")
    public void searchByNameTest() {
        List<Specializations> result = repository.searchByName("рап");
        assertThat(result.get(0).getId(), equalTo(activeSpecId1));
    }
}