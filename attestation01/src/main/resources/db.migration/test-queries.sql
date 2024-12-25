UPDATE doctor
SET work_hours_from = '2024-12-25 09:00:00', work_hours_for = '2024-12-25 17:00:00'
WHERE id = 11;

UPDATE patient_card
SET diagnosis = 'Новый диагноз'
WHERE id = 1001;

UPDATE office
SET office_type = 'Терапевтический'
WHERE id = 10;

ALTER TABLE reception
    DROP CONSTRAINT reception_patient_id_fkey,
    ADD CONSTRAINT reception_patient_id_fkey
        FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
            ON DELETE SET NULL;  -- Устанавливаем NULL при удалении

INSERT INTO reception (doctor_id, office_id, work_hours_from, work_hours_for, card_id, patient_id, insurance_id)
VALUES (10, 10, '2024-12-25 09:00:00', '2024-12-25 17:00:00', 1001, 1, 123456789);


SELECT * FROM patient_card WHERE diagnosis = 'Аллергия';
SELECT * FROM reception WHERE patient_id = 1;
SELECT * FROM office WHERE id = 1;
SELECT * FROM patient WHERE patient_id = 2;
SELECT * FROM doctor WHERE office_id = 9;


