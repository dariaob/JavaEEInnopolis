package org.dariaob.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Отправляет объект в Kafka в виде JSON строки.
     * @param topic - топик Kafka
     * @param messageObj - объект сообщения
     */
    public void sendMessage(String topic, Object messageObj) {
        try {
            String message = objectMapper.writeValueAsString(messageObj);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
