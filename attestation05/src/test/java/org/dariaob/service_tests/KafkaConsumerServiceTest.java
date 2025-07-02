package org.dariaob.service_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.kafka.KafkaConsumerService;
import org.dariaob.kafka.KafkaMessageDto;
import org.dariaob.models.Appointments;
import org.dariaob.models.DoctorScheduleSlot;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private DoctorScheduleSlotRepository slotRepository;

    @Mock
    private AppointmentsRepository appointmentsRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;


    @Test
    @DisplayName("KafkaConsumerService - Service - Обработка события удаления приёма (успех)")
    void kafkaConsumerHandleAppointmentDeletedSuccessTest() throws Exception {
        Long appointmentId = 1L;

        // Мокаем DTO из JSON
        KafkaMessageDto eventDto = new KafkaMessageDto();
        eventDto.setEventType("APPOINTMENT_DELETED");
        eventDto.setEntityId(appointmentId);

        when(objectMapper.readValue(anyString(), eq(KafkaMessageDto.class))).thenReturn(eventDto);

        // Мокаем поиск приёма с занятым слотом
        DoctorScheduleSlot slot = new DoctorScheduleSlot();
        slot.setId(10L);
        slot.setBooked(true);

        Appointments appointment = new Appointments();
        appointment.setId(appointmentId);
        appointment.setSlot(slot);

        when(appointmentsRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Выполняем метод
        kafkaConsumerService.handleAppointmentEvent("{...json...}");

        // Проверяем, что слот стал свободным и сохранился
        assertThat(slot.isBooked(), is(false));
        verify(slotRepository, times(1)).save(slot);
    }

    @Test
    @DisplayName("KafkaConsumerService - Service - Обработка события удаления приёма (приём не найден)")
    void kafkaConsumerHandleAppointmentDeletedNotFoundTest() throws Exception {
        Long appointmentId = 999L;

        KafkaMessageDto eventDto = new KafkaMessageDto();
        eventDto.setEventType("APPOINTMENT_DELETED");
        eventDto.setEntityId(appointmentId);

        when(objectMapper.readValue(anyString(), eq(KafkaMessageDto.class))).thenReturn(eventDto);
        when(appointmentsRepository.findById(appointmentId)).thenReturn(Optional.empty());

        kafkaConsumerService.handleAppointmentEvent("{...json...}");

        // Проверяем, что слот не сохранялся, т.к. приём не найден
        verify(slotRepository, never()).save(any());
    }

    @Test
    @DisplayName("KafkaConsumerService - Service - Обработка события восстановления приёма (успех)")
    void kafkaConsumerHandleAppointmentRestoredSuccessTest() throws Exception {
        Long appointmentId = 2L;

        KafkaMessageDto eventDto = new KafkaMessageDto();
        eventDto.setEventType("APPOINTMENT_RESTORED");
        eventDto.setEntityId(appointmentId);

        when(objectMapper.readValue(anyString(), eq(KafkaMessageDto.class))).thenReturn(eventDto);

        DoctorScheduleSlot slot = new DoctorScheduleSlot();
        slot.setId(20L);
        slot.setBooked(false);

        Appointments appointment = new Appointments();
        appointment.setId(appointmentId);
        appointment.setSlot(slot);

        when(appointmentsRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(slotRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        kafkaConsumerService.handleAppointmentEvent("{...json...}");

        assertThat(slot.isBooked(), is(true));
        verify(slotRepository, times(1)).save(slot);
    }

    @Test
    @DisplayName("KafkaConsumerService - Service - Обработка неизвестного типа события")
    void kafkaConsumerHandleUnknownEventTypeTest() throws Exception {
        KafkaMessageDto eventDto = new KafkaMessageDto();
        eventDto.setEventType("UNKNOWN_EVENT");
        eventDto.setEntityId(3L);

        when(objectMapper.readValue(anyString(), eq(KafkaMessageDto.class))).thenReturn(eventDto);

        kafkaConsumerService.handleAppointmentEvent("{...json...}");

        // Проверок нет, просто чтобы покрыть ветку с неизвестным событием
        verifyNoInteractions(appointmentsRepository);
        verifyNoInteractions(slotRepository);
    }
}
