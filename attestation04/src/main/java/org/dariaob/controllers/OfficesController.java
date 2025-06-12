package org.dariaob.controllers;

import org.dariaob.dto.offices.OfficeDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Offices;
import org.dariaob.services.OfficesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления кабинетами (офисами) приёма пациентов
 */
@RestController
@RequestMapping("/api/v1/offices")
@RequiredArgsConstructor
@Tag(name = "Кабинеты", description = "API для работы с кабинетами приёма")
public class OfficesController {

    private final OfficesService officesService;

    /**
     * Получить список всех активных кабинетов
     *
     * @return список DTO кабинетов
     */
    @Operation(
            summary = "Получить все активные кабинеты",
            description = "Возвращает список всех кабинетов, не помеченных как удалённые",
            tags = {"Кабинеты"}
    )
    @GetMapping
    public List<OfficeDto> getAllActiveOffices() {
        return officesService.getAllActiveOffices().stream()
                .map(OfficeDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Получить информацию о кабинете по ID
     *
     * @param id идентификатор кабинета
     * @return DTO кабинета
     * @throws DataNotFoundException если кабинет не найден или удалён
     */
    @Operation(
            summary = "Получить кабинет по ID",
            description = "Возвращает информацию о кабинете по указанному идентификатору",
            tags = {"Кабинеты"}
    )
    @GetMapping("/{id}")
    public OfficeDto getOfficeById(
            @Parameter(description = "Идентификатор кабинета", required = true)
            @PathVariable Long id) {
        return new OfficeDto(officesService.getActiveOfficeById(id));
    }

    /**
     * Создать новый кабинет
     *
     * @param officeDto DTO с данными для создания кабинета
     * @return созданный кабинет в формате DTO
     */
    @Operation(
            summary = "Создать новый кабинет",
            description = "Добавляет новый кабинет в систему",
            tags = {"Кабинеты"}
    )
    @PostMapping
    public OfficeDto createOffice(
            @Parameter(description = "Данные для создания кабинета", required = true)
            @RequestBody OfficeDto officeDto) {
        Offices office = new Offices();
        office.setName(officeDto.getName());
        officesService.saveOffice(office);
        return new OfficeDto(office);
    }

    /**
     * Обновить информацию о кабинете
     *
     * @param id        идентификатор кабинета
     * @param officeDto обновлённые данные кабинета
     * @return обновлённый кабинет в формате DTO
     */
    @Operation(
            summary = "Обновить кабинет",
            description = "Обновляет информацию о существующем кабинете",
            tags = {"Кабинеты"}
    )
    @PutMapping("/{id}")
    public OfficeDto updateOffice(
            @Parameter(description = "Идентификатор кабинета", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновлённые данные кабинета", required = true)
            @RequestBody OfficeDto officeDto) {
        Offices office = officesService.getActiveOfficeById(id);
        office.setName(officeDto.getName());
        officesService.saveOffice(office);
        return new OfficeDto(office);
    }

    /**
     * Пометить кабинет как удалённый (soft delete)
     *
     * @param id идентификатор кабинета
     * @throws DataNotFoundException если кабинет не найден или уже удалён
     */
    @Operation(
            summary = "Удалить кабинет",
            description = "Помечает кабинет как удалённый (soft delete)",
            tags = {"Кабинеты"}
    )
    @DeleteMapping("/{id}")
    public void softDeleteOffice(
            @Parameter(description = "Идентификатор кабинета", required = true)
            @PathVariable Long id) {
        officesService.softDeleteOffice(id);
    }

    /**
     * Восстановить удалённый кабинет
     *
     * @param id идентификатор кабинета
     */
    @Operation(
            summary = "Восстановить кабинет",
            description = "Снимает пометку удаления с кабинета",
            tags = {"Кабинеты"}
    )
    @PostMapping("/restore/{id}")
    public void restoreOffice(
            @Parameter(description = "Идентификатор кабинета", required = true)
            @PathVariable Long id) {
        officesService.restoreOffice(id);
    }
}