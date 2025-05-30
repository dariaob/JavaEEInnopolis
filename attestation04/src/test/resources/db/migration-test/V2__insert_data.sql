
INSERT INTO offices (id, name, is_deleted) VALUES
(1, 'Терапевтический кабинет №1', false),
(2, 'Хирургический кабинет №2', false),
(3, 'Офтальмологический кабинет №3', false),
(4, 'Неврологический кабинет №4', false),
(5, 'Кардиологический кабинет №5', false);

INSERT INTO specializations (id, name, description, is_deleted) VALUES
(1, 'Терапевт', 'Врач общей практики', false),
(2, 'Хирург', 'Оперативное лечение заболеваний', false),
(3, 'Офтальмолог', 'Лечение заболеваний глаз', false),
(4, 'Невролог', 'Лечение нервной системы', false),
(5, 'Кардиолог', 'Лечение сердечно-сосудистых заболеваний', false),
(6, 'Отоларинголог', 'Лечение ЛОР-органов', false),
(7, 'Травматолог', 'Лечение травм и повреждений', false);

INSERT INTO doctors (id, name, phone, work_hours_from, work_hours_for, office_id, is_deleted) VALUES
(1, 'Иванов Петр Сергеевич', '+79161234567', '2023-01-01 08:00:00', '2023-01-01 16:00:00', 1, false),
(2, 'Смирнова Елена Владимировна', '+79161234568', '2023-01-01 09:00:00', '2023-01-01 17:00:00', 2, false),
(3, 'Кузнецов Андрей Михайлович', '+79161234569', '2023-01-01 10:00:00', '2023-01-01 18:00:00', 3, false),
(4, 'Петрова Ольга Игоревна', '+79161234570', '2023-01-01 08:30:00', '2023-01-01 16:30:00', 4, false),
(5, 'Васильев Дмитрий Александрович', '+79161234571', '2023-01-01 07:30:00', '2023-01-01 15:30:00', 5, false);

INSERT INTO doctor_specializations (doctor_id, specialization_id) VALUES
(1, 1),
(2, 2),
(2, 7),
(3, 3),
(4, 4),
(5, 5);

INSERT INTO doctor_schedule (id, doctor_id, day_of_week, start_time, end_time, office_id, is_deleted) VALUES
(1, 1, 1, '08:00:00', '16:00:00', 1, false),
(2, 1, 2, '08:00:00', '16:00:00', 1, false),
(3, 2, 3, '09:00:00', '17:00:00', 2, false),
(4, 3, 5, '10:00:00', '18:00:00', 3, false),
(5, 4, 4, '08:30:00', '16:30:00', 4, false),
(6, 5, 5, '07:30:00', '15:30:00', 5, false);

INSERT INTO patient_cards (id, symptoms, diagnosis, meds, is_deleted) VALUES
(1, 'Головная боль, температура', 'Грипп', 'Парацетамол, витамин C', false),
(2, 'Боль в спине', 'Остеохондроз', 'Диклофенак, массаж', false),
(3, 'Покраснение глаз, зуд', 'Конъюнктивит', 'Глазные капли', false),
(4, 'Головокружение, тошнота', 'Вегетососудистая дистония', 'Глицин, отдых', false),
(5, 'Боль в груди', 'Стенокардия', 'Нитроглицерин', false);

INSERT INTO patients (id, name, birth_date, phone, patient_card_id, is_deleted, insurance_id) VALUES
(1, 'Сидоров Алексей Николаевич', '1985-05-15', '+79161234572', 1, false, 1001),
(2, 'Ковалева Марина Викторовна', '1990-08-22', '+79161234573', 2, false, 1002),
(3, 'Федоров Игорь Станиславович', '1978-11-30', '+79161234574', 3, false, 1003),
(4, 'Михайлова Анна Сергеевна', '1995-03-10', '+79161234575', 4, false, 1004),
(5, 'Николаев Владислав Олегович', '1982-07-18', '+79161234576', 5, false, 1005);

INSERT INTO appointments (id, date, doctor_id, patient_id, work_hours_from, work_hours_for, is_deleted, card_id, insurance_id, office_id) VALUES
(1, '2023-06-01 09:00:00', 1, 1, '2023-06-01 09:00:00', '2023-06-01 09:30:00', false, 1, 1001, 1),
(2, '2023-06-01 10:00:00', 2, 2, '2023-06-01 10:00:00', '2023-06-01 10:30:00', false, 2, 1002, 2),
(3, '2023-06-02 11:00:00', 3, 3, '2023-06-02 11:00:00', '2023-06-02 11:30:00', false, 3, 1003, 3),
(4, '2023-06-02 14:00:00', 4, 4, '2023-06-02 14:00:00', '2023-06-02 14:30:00', false, 4, 1004, 4),
(5, '2023-06-03 15:00:00', 5, 5, '2023-06-03 15:00:00', '2023-06-03 15:30:00', false, 5, 1005, 5);

INSERT INTO patient_cards_history (id, card_id, changed_at, changed_by, old_diagnosis, new_diagnosis, old_meds, new_meds, change_reason) VALUES
(1, 1, '2023-05-15 10:00:00', 'doctor1', 'ОРВИ', 'Грипп', 'Аспирин', 'Парацетамол, витамин C', 'Уточнение диагноза'),
(2, 2, '2023-05-20 11:30:00', 'doctor2', 'Растяжение мышц', 'Остеохондроз', 'Мази', 'Диклофенак, массаж', 'Результаты МРТ'),
(3, 3, '2023-05-10 09:15:00', 'doctor3', NULL, 'Конъюнктивит', NULL, 'Глазные капли', 'Первичный диагноз'),
(4, 4, '2023-05-25 14:45:00', 'doctor4', 'Мигрень', 'Вегетососудистая дистония', 'Обезболивающие', 'Глицин, отдых', 'Консультация невролога'),
(5, 5, '2023-06-01 16:30:00', 'doctor5', NULL, 'Стенокардия', NULL, 'Нитроглицерин', 'Результаты ЭКГ');

INSERT INTO users (username, password, roles, is_deleted) VALUES
('admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ROLE_ADMIN', false), -- admin123
('doctor1', '$2a$10$sOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z', 'ROLE_DOCTOR', false), -- doctor1
('doctor2', '$2a$10$sOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z', 'ROLE_DOCTOR', false), -- doctor2
('patient1', '$2a$10$sOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z', 'ROLE_USER', false), -- patient1
('reception', '$2a$10$sOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z5z5z5z5uOZ5VZx5z5z5z', 'ROLE_RECEPTION', false); -- reception1

ALTER TABLE offices ALTER COLUMN id RESTART WITH 6;
ALTER TABLE specializations ALTER COLUMN id RESTART WITH 8;
ALTER TABLE doctors ALTER COLUMN id RESTART WITH 6;
ALTER TABLE doctor_schedule ALTER COLUMN id RESTART WITH 7;
ALTER TABLE patient_cards ALTER COLUMN id RESTART WITH 6;
ALTER TABLE patients ALTER COLUMN id RESTART WITH 6;
ALTER TABLE appointments ALTER COLUMN id RESTART WITH 6;
ALTER TABLE patient_cards_history ALTER COLUMN id RESTART WITH 6;