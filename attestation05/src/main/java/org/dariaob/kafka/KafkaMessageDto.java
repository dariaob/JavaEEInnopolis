package org.dariaob.kafka;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO для передачи событий в Kafka.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessageDto {

    /**
     * Тип события
     */
    private String eventType;

    /**
     * Идентификатор сущности, к которой относится событие.
     */
    private Long entityId;

    /**
     * Время возникновения события.
     */
    private LocalDateTime eventTime;

    /**
     * Полезная нагрузка события — данные, связанные с изменением.
     * Может быть сериализованным JSON-объектом или конкретным DTO.
     */
    private String payload;
}
