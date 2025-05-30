-- Создаём схему, если она ещё не существует
CREATE SCHEMA IF NOT EXISTS clinic_db;

-- Настройка схемы по умолчанию для следующих CREATE TABLE
SET search_path TO clinic_db;

-- Таблица офисов
CREATE TABLE IF NOT EXISTS offices (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN NOT NULL
);

COMMENT ON TABLE offices IS 'Таблица с данными об кабинетах приема';
COMMENT ON COLUMN offices.id IS 'Идентификатор кабинета';
COMMENT ON COLUMN offices.name IS 'Название кабинета';
COMMENT ON COLUMN offices.is_deleted IS 'Признак удаления кабинета';

-- Таблица врачей
CREATE TABLE IF NOT EXISTS doctors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    work_hours_from TIMESTAMP NOT NULL,
    work_hours_for TIMESTAMP NOT NULL,
    office_id BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    FOREIGN KEY (office_id) REFERENCES offices(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE doctors IS 'Таблица с данными о враче';
COMMENT ON COLUMN doctors.id IS 'Идентификатор врача';
COMMENT ON COLUMN doctors.name IS 'Имя врача';
COMMENT ON COLUMN doctors.phone IS 'Телефон врача';
COMMENT ON COLUMN doctors.work_hours_from IS 'Время начала работы врача';
COMMENT ON COLUMN doctors.work_hours_for IS 'Время окончания работы врача';
COMMENT ON COLUMN doctors.office_id IS 'Идентификатор офиса';
COMMENT ON COLUMN doctors.is_deleted IS 'Признак удаления врача';

-- Таблица карт пациентов
CREATE TABLE IF NOT EXISTS patient_cards (
    id SERIAL PRIMARY KEY,
    symptoms VARCHAR(255) NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    meds VARCHAR(255),
    is_deleted BOOLEAN NOT NULL
);

COMMENT ON TABLE patient_cards IS 'Таблица с данными о картах пациентов';
COMMENT ON COLUMN patient_cards.id IS 'Идентификатор карты пациента';
COMMENT ON COLUMN patient_cards.symptoms IS 'Симптомы';
COMMENT ON COLUMN patient_cards.diagnosis IS 'Диагноз';
COMMENT ON COLUMN patient_cards.meds IS 'Лекарства';
COMMENT ON COLUMN patient_cards.is_deleted IS 'Признак удаления карты пациента';

-- Таблица пациентов
CREATE TABLE IF NOT EXISTS patients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    patient_card_id BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    insurance_id BIGINT NOT NULL,
    FOREIGN KEY (patient_card_id) REFERENCES patient_cards(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE patients IS 'Таблица с данными о пациенте';
COMMENT ON COLUMN patients.id IS 'Идентификатор пациента';
COMMENT ON COLUMN patients.name IS 'Имя пациента';
COMMENT ON COLUMN patients.birth_date IS 'Дата рождения пациента';
COMMENT ON COLUMN patients.phone IS 'Телефон пациента';
COMMENT ON COLUMN patients.patient_card_id IS 'Идентификатор карты пациента';
COMMENT ON COLUMN patients.is_deleted IS 'Признак удаления пациента';
COMMENT ON COLUMN patients.insurance_id IS 'Идентификатор страхового полиса';

-- Таблица приемов
CREATE TABLE IF NOT EXISTS appointments (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    work_hours_from TIMESTAMP NOT NULL,
    work_hours_for TIMESTAMP NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    card_id BIGINT NOT NULL,
    insurance_id BIGINT NOT NULL,
    office_id BIGINT NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (card_id) REFERENCES patient_cards(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (office_id) REFERENCES offices(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE appointments IS 'Таблица с данными о приеме';
COMMENT ON COLUMN appointments.id IS 'Идентификатор приема';
COMMENT ON COLUMN appointments.date IS 'Дата приема';
COMMENT ON COLUMN appointments.doctor_id IS 'Идентификатор врача';
COMMENT ON COLUMN appointments.patient_id IS 'Идентификатор пациента';
COMMENT ON COLUMN appointments.work_hours_from IS 'Время начала приема';
COMMENT ON COLUMN appointments.work_hours_for IS 'Время окончания приема';
COMMENT ON COLUMN appointments.is_deleted IS 'Признак удаления приема';
COMMENT ON COLUMN appointments.card_id IS 'Идентификатор карты пациента';
COMMENT ON COLUMN appointments.insurance_id IS 'Идентификатор страхового полиса';
COMMENT ON COLUMN appointments.office_id IS 'Идентификатор кабинета приема';

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(30) PRIMARY KEY,
    password VARCHAR NOT NULL,
    roles VARCHAR[] NOT NULL,
    is_deleted BOOLEAN NOT NULL
);

COMMENT ON TABLE users IS 'Таблица с данными о пользователях';
COMMENT ON COLUMN users.username IS 'Имя пользователя';
COMMENT ON COLUMN users.password IS 'Пароль пользователя';
COMMENT ON COLUMN users.roles IS 'Роли пользователя';

-- Таблица расписания врачей
CREATE TABLE IF NOT EXISTS doctor_schedule (
    id SERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    office_id BIGINT,
    is_deleted BOOLEAN NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (office_id) REFERENCES offices(id) ON DELETE SET NULL
);

COMMENT ON TABLE doctor_schedule IS 'Таблица с данными о расписании врачей';
COMMENT ON COLUMN doctor_schedule.id IS 'Идентификатор записи в расписании';
COMMENT ON COLUMN doctor_schedule.doctor_id IS 'Идентификатор врача';
COMMENT ON COLUMN doctor_schedule.day_of_week IS 'День недели';
COMMENT ON COLUMN doctor_schedule.start_time IS 'Время начала приема';
COMMENT ON COLUMN doctor_schedule.end_time IS 'Время окончания приема';
COMMENT ON COLUMN doctor_schedule.office_id IS 'Идентификатор кабинета';
COMMENT ON COLUMN doctor_schedule.is_deleted IS 'Признак удаления записи';

-- История изменений карт пациентов
CREATE TABLE IF NOT EXISTS patient_cards_history (
    id SERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(50),
    old_diagnosis VARCHAR(255),
    new_diagnosis VARCHAR(255) NOT NULL,
    old_meds TEXT,
    new_meds TEXT,
    change_reason TEXT,
    FOREIGN KEY (card_id) REFERENCES patient_cards(id) ON DELETE CASCADE
);

COMMENT ON TABLE patient_cards_history IS 'История изменений карт пациентов';
COMMENT ON COLUMN patient_cards_history.card_id IS 'Ссылка на карту пациента';
COMMENT ON COLUMN patient_cards_history.changed_at IS 'Время изменения записи';
COMMENT ON COLUMN patient_cards_history.changed_by IS 'Пользователь, внесший изменения';
COMMENT ON COLUMN patient_cards_history.old_diagnosis IS 'Предыдущий диагноз';
COMMENT ON COLUMN patient_cards_history.new_diagnosis IS 'Новый диагноз';
COMMENT ON COLUMN patient_cards_history.change_reason IS 'Причина изменения';

-- Таблица специализаций
CREATE TABLE IF NOT EXISTS specializations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE specializations IS 'Справочник медицинских специализаций';
COMMENT ON COLUMN specializations.name IS 'Название специализации';
COMMENT ON COLUMN specializations.description IS 'Описание специализации';

-- Таблица связи врачей и специализаций
CREATE TABLE IF NOT EXISTS doctor_specializations (
    doctor_id BIGINT NOT NULL,
    specialization_id BIGINT NOT NULL,
    PRIMARY KEY (doctor_id, specialization_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (specialization_id) REFERENCES specializations(id) ON DELETE CASCADE
);

COMMENT ON TABLE doctor_specializations IS 'Связь врачей со специализациями';
COMMENT ON COLUMN doctor_specializations.doctor_id IS 'Идентификатор врача';
COMMENT ON COLUMN doctor_specializations.specialization_id IS 'Идентификатор специализации';
