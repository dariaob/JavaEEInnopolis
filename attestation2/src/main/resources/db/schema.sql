CREATE SCHEMA reg_db;
CREATE TABLE reg_db.office (
                               "id" bigserial PRIMARY KEY NOT NULL,
                               "office_type" varchar(255)
);
-- Карта больного
CREATE TABLE IF NOT EXISTS reg_db.patient_card (
                                            "id"        bigserial primary key not null,     -- Номер(id) карты
                                            "symptoms"  varchar(255),                       -- Симптомы
                                            "diagnosis" varchar(255),                       -- Диагноз
                                            "medicine"  varchar(255)                        -- Лекарства
);

-- Информация по врачу
CREATE TABLE IF NOT EXISTS reg_db.doctor (
                                      "id"              bigserial primary key not null,   -- id Врача
                                      "name"            varchar(255) not null,             -- ФИО врача
                                      "work_hours_from" timestamp not null,               -- Начало времени приема
                                      "work_hours_for"  timestamp not null,               -- Время конца приема
                                      "office_id"       bigint not null,                  -- Номер (id) кабинета
                                      FOREIGN KEY ("office_id") REFERENCES office ("id")  -- Ссылка на таблицу office
);

-- Данные о пациенте
CREATE TABLE IF NOT EXISTS reg_db.patient (
                                       "patient_id"   bigserial PRIMARY KEY,               -- Уникальный идентификатор для пациента
                                       "insurance_id" bigint NOT NULL,                     -- Не уникальный страховой номер
                                       "name"         varchar(255) not null,               -- ФИО пациента
                                       "address"      varchar(255),                        -- Место жительства
                                       "card_id"      bigint not null,                     -- Номер карты пациента
                                       FOREIGN KEY ("card_id") REFERENCES reg_db.patient_card ("id") -- Внешний ключ на patient_card
);

-- Данные приёма
CREATE TABLE IF NOT EXISTS reg_db.reception (
                                         "id"              bigserial primary key not null,    -- Номер приёма
                                         "doctor_id"       bigint not null,                   -- Номер врача
                                         "office_id"       bigint not null,                   -- Номер кабинета
                                         "work_hours_from" timestamp,                         -- Начало приема
                                         "work_hours_for"  timestamp,                         -- Конец приема
                                         "card_id"         bigint not null,                   -- Номер карты пациента
                                         "patient_id"      bigint not null,                   -- Используем уникальный идентификатор пациента
                                         "insurance_id"    bigint not null,                   -- Страховой номер
                                         FOREIGN KEY ("doctor_id") REFERENCES reg_db.doctor ("id"),  -- Внешний ключ на таблицу doctor
                                         FOREIGN KEY ("office_id") REFERENCES reg_db.office ("id"),  -- Внешний ключ на таблицу office
                                         FOREIGN KEY ("patient_id") REFERENCES reg_db.patient ("patient_id"),  -- Ссылка на пациента
                                         FOREIGN KEY ("card_id") REFERENCES reg_db.patient_card ("id") -- Внешний ключ на таблицу patient_card
);