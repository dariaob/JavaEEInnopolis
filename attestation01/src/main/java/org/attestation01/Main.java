package org.attestation01;

import repository.*;
import repository.impl.*;

import java.time.LocalDateTime;

public class Main {
    private static final ReceptionRepository receptionRepository = new ReceptionRepositoryImpl();           // Прием
    public static final OfficeRepository officeRepository = new OfficeRepositoryImpl();                     // Кабинет
    public static final DoctorRepository doctorRepository = new DoctorRepositoryImpl();                     // Врач
    public static final PatientCardRepository patientCardRepository = new PatientCardRepositoryImpl();      // Прием
    public static final PatientRepository patientRepository = new PatientRepositoryImpl();                  // Пациент
    public static void main(String[] args) {

        // Добавляем карту пациента
        Long patientCardId = 3001L;
        String symptoms = "Акне, сыпь";
        String diagnosis = "Аллергия";
        String medicine = "Цетрин";
        patientCardRepository.insertRow(patientCardId, symptoms, diagnosis, medicine);

        // Добавляем нового пациента
        patientRepository.insertRow(123456789L, "Федоров Иван Петрович", "Москва, ул. Ленина, д. 10", 3001L);

        // Добавляем кабинет
        officeRepository.insertRow(11L, "Кабинет дерматолога");
        // Удаляем кабинеты
        officeRepository.deleteRow(11L);

        // Добавляем нового врача
        String name = "Доктор Кузьмин Иван Иванович";
        LocalDateTime workHoursFrom = LocalDateTime.of(2024, 12, 25, 9, 0);
        LocalDateTime workHoursFor = LocalDateTime.of(2024, 12, 25, 17, 0);
        Long officeId = 10L;
        doctorRepository.insertRow(name, workHoursFrom, workHoursFor, officeId);

        // Добавляем новые данные о приёме
        Long patientId = 11L;                 // ID пациента
        Long doctorId = 9L;
        Long insuranceId = 123456789L;       // Страховой номер
        receptionRepository.insertRow(doctorId, officeId, workHoursFrom, workHoursFor, patientCardId, patientId, insuranceId);
        receptionRepository.deleteRow(2L);

        // Все пациенты
        System.out.println(patientRepository.findAll());
        // Все карты пациентов
        System.out.println(patientRepository.findAll());
        // Все врачи
        System.out.println(doctorRepository.findAll());
        // Все приёмы
        System.out.println(receptionRepository.findAll());
        // Все кабинеты
        System.out.println(officeRepository.findAll());
    }
}