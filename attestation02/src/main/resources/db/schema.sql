-- Данные кабинета приема
CREATE TABLE IF NOT EXISTS office
(
    "id"          bigint primary key not null, -- Номер(id) кабинета
    "office_type" varchar(255)                 -- Назначение кабинета
);

CREATE TABLE IF NOT EXISTS patient_card
(                                            -- Карта больного
    "id"        bigint primary key not null, -- Номер(id) карты
    "symptoms"  varchar(255),                -- Симптомы
    "diagnosis" varchar(255),                -- Диагноз
    "medicine"  varchar(255)                 -- Лекарства
);

CREATE TABLE IF NOT EXISTS doctor
(                                                     -- Информация по врачу
    "id"              bigserial primary key not null, -- id Врача
    "name"            varchar(255)          not null, -- ФИО врача
    "work_hours_from" timestamp             not null, -- Начало времени приема
    "work_hours_for"  timestamp             not null, -- Время конца приема
    "office_id"       bigint                not null, -- Номер (id) кабинета
    FOREIGN KEY ("office_id") REFERENCES office (id)  -- Ссылка на таблицу office
);

CREATE TABLE IF NOT EXISTS patient
(                                                        -- Данные о пациенте
    "patient_id"   SERIAL PRIMARY KEY,                   -- Уникальный идентификатор для пациента
    "insurance_id" bigint       NOT NULL,                -- Не уникальный страховой номер
    "name"         varchar(255) not null,                -- ФИО пациента
    "address"      varchar(255),                         -- Место жительства
    "card_id"      bigint       not null,                -- Номер карты пациента
    FOREIGN KEY ("card_id") REFERENCES patient_card (id) -- Внешний ключ на patient_card
);

CREATE TABLE IF NOT EXISTS reception -- Данные о приёме
(                                                              -- Данные приёма
    "id"              bigserial primary key not null,          -- Номер приёма
    "doctor_id"       bigint                not null,          -- Номер врача
    "office_id"       bigint                not null,          -- Номер кабинета
    "work_hours_from" timestamp,                               -- Начало приема
    "work_hours_for"  timestamp,                               -- Конец приема
    "card_id"         bigint                not null,          -- Номер карты пациента
    "patient_id"      bigint                not null,          -- Используем уникальный идентификатор пациента
    "insurance_id"    bigint                not null,          -- Страховой номер
    FOREIGN KEY ("doctor_id") REFERENCES doctor (id),          -- Внешний ключ на таблицу doctor
    FOREIGN KEY ("office_id") REFERENCES office (id),          -- Внешний ключ на таблицу office
    FOREIGN KEY ("patient_id") REFERENCES patient (patient_id) -- Ссылка на пациента
);