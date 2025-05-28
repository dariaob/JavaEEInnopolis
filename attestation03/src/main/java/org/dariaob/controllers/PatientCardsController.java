package org.dariaob.controllers;

import org.dariaob.dto.patientCards.PatientCardRequestDto;
import org.dariaob.dto.patientCards.PatientCardResponseDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.services.PatientCardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления медицинскими картами пациентов.
 */
@RestController
@RequestMapping("/api/v1/patient-cards")
@RequiredArgsConstructor
@Tag(name = "Медицинские карты", description = "API для управления медицинскими картами пациентов")
public class PatientCardsController {

    private final PatientCardsService patientCardsService;

    /**
     * Получить все активные медицинские карты
     * @return список карт в формате DTO
     */
    @Operation(
            summary = "Получить все активные карты",
            description = "Возвращает список всех медицинских карт, не помеченных как удаленные",
            tags = {"Медицинские карты"}
    )
    @GetMapping
    public List<PatientCardResponseDto> getAllActiveCards() {
        return patientCardsService.getAllActive().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить медицинскую карту по ID
     * @param id идентификатор карты
     * @return данные карты в формате DTO
     * @throws DataNotFoundException если карта не найдена или удалена
     */
    @Operation(
            summary = "Получить карту по ID",
            description = "Возвращает полные данные медицинской карты по указанному идентификатору",
            tags = {"Медицинские карты"}
    )
    @GetMapping("/{id}")
    public PatientCardResponseDto getCardById(
            @Parameter(description = "Уникальный идентификатор карты", required = true)
            @PathVariable Long id) {
        return convertToDto(patientCardsService.getActiveById(id));
    }

    /**
     * Получить медицинскую карту по ID пациента
     * @param patientId идентификатор пациента
     * @return данные карты в формате DTO
     * @throws DataNotFoundException если карта не найдена или удалена
     */
    @Operation(
            summary = "Получить карту по ID пациента",
            description = "Возвращает медицинскую карту, привязанную к указанному пациенту",
            tags = {"Медицинские карты"}
    )
    @GetMapping("/by-patient/{patientId}")
    public PatientCardResponseDto getCardByPatientId(
            @Parameter(description = "ID пациента", required = true)
            @PathVariable Long patientId) {
        return convertToDto(patientCardsService.getByPatientId(patientId));
    }

    /**
     * Найти медицинские карты по диагнозу
     * @param diagnosis часть диагноза для поиска
     * @return список подходящих карт в формате DTO
     */
    @Operation(
            summary = "Поиск карт по диагнозу",
            description = "Возвращает медицинские карты, содержащие указанную часть диагноза",
            tags = {"Медицинские карты"}
    )
    @GetMapping("/search")
    public List<PatientCardResponseDto> searchByDiagnosis(
            @Parameter(description = "Часть диагноза для поиска", required = true)
            @RequestParam String diagnosis) {
        return patientCardsService.findByDiagnosis(diagnosis).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Создать новую медицинскую карту
     * @param requestDto DTO с данными для создания карты
     * @return созданная карта в формате DTO
     */
    @Operation(
            summary = "Создать новую карту",
            description = "Создает новую медицинскую карту пациента",
            tags = {"Медицинские карты"}
    )
    @PostMapping
    public PatientCardResponseDto createCard(
            @Parameter(description = "Данные для создания карты", required = true)
            @RequestBody PatientCardRequestDto requestDto) {
        PatientCards card = new PatientCards();
        updateCardFromDto(card, requestDto);
        card.setDeleted(false);
        PatientCards savedCard = patientCardsService.save(card);
        return convertToDto(savedCard);
    }

    /**
     * Обновить медицинскую карту
     * @param id идентификатор карты
     * @param requestDto обновленные данные карты
     * @return обновленная карта в формате DTO
     * @throws DataNotFoundException если карта не найдена или удалена
     */
    @Operation(
            summary = "Обновить карту",
            description = "Обновляет данные медицинской карты",
            tags = {"Медицинские карты"}
    )
    @PutMapping("/{id}")
    public PatientCardResponseDto updateCard(
            @Parameter(description = "ID обновляемой карты", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные карты", required = true)
            @RequestBody PatientCardRequestDto requestDto) {
        PatientCards card = patientCardsService.getActiveById(id);
        updateCardFromDto(card, requestDto);
        PatientCards updatedCard = patientCardsService.save(card);
        return convertToDto(updatedCard);
    }

    /**
     * Пометить медицинскую карту как удаленную (soft delete)
     * @param id идентификатор карты
     * @throws DataNotFoundException если карта не найдена или уже удалена
     */
    @Operation(
            summary = "Удалить карту",
            description = "Помечает медицинскую карту как удаленную без физического удаления данных",
            tags = {"Медицинские карты"}
    )
    @DeleteMapping("/{id}")
    public void deleteCard(
            @Parameter(description = "ID удаляемой карты", required = true)
            @PathVariable Long id) {
        patientCardsService.softDelete(id);
    }

    /**
     * Восстановить удаленную медицинскую карту
     * @param id идентификатор карты
     * @throws DataNotFoundException если карта не найдена или уже активна
     */
    @Operation(
            summary = "Восстановить карту",
            description = "Снимает отметку об удалении с медицинской карты",
            tags = {"Медицинские карты"}
    )
    @PostMapping("/restore/{id}")
    public void restoreCard(
            @Parameter(description = "ID восстанавливаемой карты", required = true)
            @PathVariable Long id) {
        patientCardsService.restore(id);
    }

    /**
     * Получить историю изменений медицинской карты
     * @param cardId идентификатор карты
     * @return список изменений карты
     */
    @Operation(
            summary = "Получить историю изменений",
            description = "Возвращает историю всех изменений медицинской карты",
            tags = {"Медицинские карты"}
    )
    @GetMapping("/{cardId}/history")
    public List<PatientCardsHistory> getCardHistory(
            @Parameter(description = "ID карты", required = true)
            @PathVariable Long cardId) {
        return patientCardsService.getHistoryByCardId(cardId);
    }

    /**
     * Получить последнее изменение медицинской карты
     * @param cardId идентификатор карты
     * @return последнее изменение карты
     * @throws DataNotFoundException если история изменений не найдена
     */
    @Operation(
            summary = "Получить последнее изменение",
            description = "Возвращает последнее изменение медицинской карты",
            tags = {"Медицинские карты"}
    )
    @GetMapping("/{cardId}/last-change")
    public PatientCardsHistory getLastCardChange(
            @Parameter(description = "ID карты", required = true)
            @PathVariable Long cardId) {
        return patientCardsService.getLastChangeByCardId(cardId);
    }

    /**
     * Преобразует сущность PatientCards в DTO
     * @param card сущность медицинской карты
     * @return DTO медицинской карты
     */
    private PatientCardResponseDto convertToDto(PatientCards card) {
        PatientCardResponseDto dto = new PatientCardResponseDto();
        dto.setId(card.getId());
        dto.setSymptoms(card.getSymptoms());
        dto.setDiagnosis(card.getDiagnosis());
        dto.setMeds(card.getMeds());
        return dto;
    }

    /**
     * Обновляет данные карты из DTO
     * @param card сущность для обновления
     * @param dto DTO с новыми данными
     */
    private void updateCardFromDto(PatientCards card, PatientCardRequestDto dto) {
        card.setSymptoms(dto.getSymptoms());
        card.setDiagnosis(dto.getDiagnosis());
        card.setMeds(dto.getMeds());
    }
}