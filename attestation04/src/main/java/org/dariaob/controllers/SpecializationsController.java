package org.dariaob.controllers;

import org.dariaob.dto.specializations.SpecializationDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Specializations;
import org.dariaob.services.SpecializationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления медицинскими специализациями врачей
 */
@RestController
@RequestMapping("/api/v1/specializations")
@RequiredArgsConstructor
@Tag(name = "Специализации", description = "API для работы с медицинскими специализациями")
public class SpecializationsController {

    private final SpecializationsService specializationsService;

    /**
     * Получить список всех активных специализаций
     *
     * @return список DTO специализаций
     */
    @Operation(
            summary = "Получить все активные специализации",
            description = "Возвращает список всех специализаций, не помеченных как удалённые",
            tags = {"Специализации"}
    )
    @GetMapping
    public List<SpecializationDto> getAllActive() {
        return specializationsService.getAllActive().stream()
                .map(SpecializationDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Получить специализацию по ID
     *
     * @param id идентификатор специализации
     * @return DTO специализации
     * @throws DataNotFoundException если специализация не найдена или удалена
     */
    @Operation(
            summary = "Получить специализацию по ID",
            description = "Возвращает информацию о специализации по указанному идентификатору",
            tags = {"Специализации"}
    )
    @GetMapping("/{id}")
    public SpecializationDto getById(
            @Parameter(description = "Идентификатор специализации", required = true)
            @PathVariable Long id) {
        return new SpecializationDto(specializationsService.getActiveById(id));
    }

    /**
     * Получить специализацию по названию
     *
     * @param name название специализации
     * @return DTO специализации
     * @throws DataNotFoundException если специализация не найдена или удалена
     */
    @Operation(
            summary = "Получить специализацию по названию",
            description = "Возвращает информацию о специализации по точному названию (без учёта регистра)",
            tags = {"Специализации"}
    )
    @GetMapping("/by-name/{name}")
    public SpecializationDto getByName(
            @Parameter(description = "Название специализации", required = true)
            @PathVariable String name) {
        return new SpecializationDto(specializationsService.getByNameIgnoreCase(name));
    }

    /**
     * Поиск специализаций по части названия
     *
     * @param namePart часть названия для поиска
     * @return список подходящих специализаций
     */
    @Operation(
            summary = "Поиск специализаций по названию",
            description = "Возвращает список специализаций, содержащих указанную часть названия",
            tags = {"Специализации"}
    )
    @GetMapping("/search")
    public List<SpecializationDto> searchByName(
            @Parameter(description = "Часть названия для поиска", required = true)
            @RequestParam String namePart) {
        return specializationsService.searchByName(namePart).stream()
                .map(SpecializationDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Создать новую специализацию
     *
     * @param specializationDto DTO с данными для создания специализации
     * @return созданная специализация в формате DTO
     */
    @Operation(
            summary = "Создать новую специализацию",
            description = "Добавляет новую специализацию в систему",
            tags = {"Специализации"}
    )
    @PostMapping
    public SpecializationDto create(
            @Parameter(description = "Данные для создания специализации", required = true)
            @RequestBody SpecializationDto specializationDto) {
        Specializations specialization = new Specializations();
        specialization.setName(specializationDto.getName());
        specializationsService.save(specialization);
        return new SpecializationDto(specialization);
    }

    /**
     * Обновить информацию о специализации
     *
     * @param id                идентификатор специализации
     * @param specializationDto обновлённые данные специализации
     * @return обновлённая специализация в формате DTO
     */
    @Operation(
            summary = "Обновить специализацию",
            description = "Обновляет информацию о существующей специализации",
            tags = {"Специализации"}
    )
    @PutMapping("/{id}")
    public SpecializationDto update(
            @Parameter(description = "Идентификатор специализации", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновлённые данные специализации", required = true)
            @RequestBody SpecializationDto specializationDto) {
        Specializations specialization = specializationsService.getActiveById(id);
        specialization.setName(specializationDto.getName());
        specializationsService.save(specialization);
        return new SpecializationDto(specialization);
    }

    /**
     * Пометить специализацию как удалённую (soft delete)
     *
     * @param id идентификатор специализации
     * @throws DataNotFoundException если специализация не найдена или уже удалена
     */
    @Operation(
            summary = "Удалить специализацию",
            description = "Помечает специализацию как удалённую (soft delete)",
            tags = {"Специализации"}
    )
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Идентификатор специализации", required = true)
            @PathVariable Long id) {
        specializationsService.softDelete(id);
    }

    /**
     * Восстановить удалённую специализацию
     *
     * @param id идентификатор специализации
     */
    @Operation(
            summary = "Восстановить специализацию",
            description = "Снимает пометку удаления со специализации",
            tags = {"Специализации"}
    )
    @PostMapping("/restore/{id}")
    public void restore(
            @Parameter(description = "Идентификатор специализации", required = true)
            @PathVariable Long id) {
        specializationsService.restore(id);
    }
}