package org.dariaob.controllers;

import org.dariaob.dto.patientCardsHistory.PatientCardHistoryDto;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.services.PatientCardsHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для просмотра истории изменений медицинских карт
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient-cards/history")
public class PatientCardsHistoryController {

    private final PatientCardsHistoryService historyService;

    /**
     * Получить все изменения для конкретной карты
     *
     * @param cardId ID медицинской карты
     * @return список изменений от новых к старым
     */
    @GetMapping("/{cardId}")
    public List<PatientCardHistoryDto> getCardHistory(@PathVariable Long cardId) {
        return historyService.getByCardId(cardId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Получить последнее изменение карты
     *
     * @param cardId ID медицинской карты
     * @return последняя запись в истории
     */
    @GetMapping("/{cardId}/last")
    public PatientCardHistoryDto getLastChange(@PathVariable Long cardId) {
        return historyService.getLastChangeByCardId(cardId)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * Преобразует запись истории в DTO
     */
    private PatientCardHistoryDto convertToDto(PatientCardsHistory history) {
        PatientCardHistoryDto dto = new PatientCardHistoryDto();
        dto.setId(history.getId());
        dto.setChangedAt(history.getChangedAt());
        dto.setChangedBy(history.getChangedBy());
        dto.setOldDiagnosis(history.getOldDiagnosis());
        dto.setNewDiagnosis(history.getNewDiagnosis());
        dto.setOldMeds(history.getOldMeds());
        dto.setNewMeds(history.getNewMeds());
        return dto;
    }
}