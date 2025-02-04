CREATE TABLE IF NOT EXISTS products(
    "id" bigserial PRIMARY KEY,
    "name" varchar(255) NOT NULL,
    "amount" int NOT NULL,
    "price" int NOT NULL,
    "purchase_date" timestamp NOT NULL
)