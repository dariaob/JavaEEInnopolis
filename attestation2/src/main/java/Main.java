import exceptions.ImpossibleToDeleteException;
import exceptions.ObjectNotFountException;
import repository.*;
import repository.impl.*;

import java.time.LocalDateTime;

public class Main {
    public static final OfficeRepository officeRepository = new OfficeRepositoryImpl();
    public static final PatientCardRepository patientCards = new PatientCardRepositoryImpl();
    public static final DoctorRepository doctors = new DoctorRepositoryImpl();
    public static final PatientRepository patientRepository = new PatientRepositoryImpl();
    public static final ReceptionRepository reception = new ReceptionRepositoryImpl();
    public static void main(String[] args) throws ObjectNotFountException, ImpossibleToDeleteException {
        // Кабинеты
        System.out.println(officeRepository.create("Приемная"));
        officeRepository.update("Йогурт", 1L);
        System.out.println(officeRepository.findById(1L));
        officeRepository.deleteById(1L);
        System.out.println(officeRepository.findAll());
        System.out.println(officeRepository.findByOfficeType("Йогурт"));

        // Карточки пациента
        System.out.println(patientCards.create("Кашель, насморк, головная боль", "ОРВИ", "Аспирин"));
        System.out.println(patientCards.create("Боль в спине", "Недостаток витамина B", "Комбилепен"));
        patientCards.update("Кашель, насморк, головная боль, температура, боль в горле", "Ангина", "Аспирин, антибиотики", 1L);
        System.out.println(patientCards.findById(2L));
        patientCards.deleteById(1L);
        System.out.println(patientCards.findByDiagnosis("Ангина"));
        System.out.println(patientCards.findAll());

        // Врач
        LocalDateTime workHoursFrom = LocalDateTime.of(2025, 1, 17, 9, 0); // 9:00
        LocalDateTime workHoursFor = LocalDateTime.of(2025, 1, 17, 17, 0); // 17:00
        LocalDateTime workHoursFrom2 = LocalDateTime.of(2025, 1, 17, 10, 0); // 10:00
        LocalDateTime workHoursFor2 = LocalDateTime.of(2025, 1, 17, 18, 0); // 18:00
        System.out.println(doctors.create("Иванов Иван Иванович", workHoursFrom, workHoursFor, 3L));
        System.out.println(doctors.create("Петров Петр Петрович", workHoursFrom2, workHoursFor2, 2L));
        doctors.update("Воронова Ольга Михайловна", workHoursFrom, workHoursFor2, 2L, 1L);
        System.out.println(doctors.findById(5L));
        doctors.deleteById(2L);
        System.out.println(doctors.findByOffice(2L));
        System.out.println(doctors.findAll());

        // Пациент
        System.out.println(patientRepository.create(123456789L, "Косяков Федор Михайлович", "Монастырская 23", 4L));
        System.out.println(patientRepository.create(987654321L, "Васильков Федор Михайлович", "Монастырская 123", 3L));
        patientRepository.update(1234789L, "Косяков Федор Михайлович", "Монастырская 12", 2L, 2L);
        System.out.println(patientRepository.findById(2L));
        System.out.println(patientRepository.deleteById(1L, 1L));
        System.out.println(patientRepository.findByDInsuranceId(987654321L));
        System.out.println(patientRepository.findAll());

        // Прием
        System.out.println(reception.create(3L, 2L, workHoursFrom, workHoursFor, 2L, 2L, 987654321L));
        System.out.println(reception.create(5L, 3L, workHoursFrom2, workHoursFor2, 3L, 2L, 123456789L));
        reception.update(5L, 3L, workHoursFrom2, workHoursFor, 3L, 2L, 123456789L, 2L);
        System.out.println(reception.findById(2L));
        System.out.println(reception.deleteById(1L, 1L, 1L, 1L, 1L));
        System.out.println(reception.findByDoctorId(5L));
        System.out.println(reception.findAll());
    }
}