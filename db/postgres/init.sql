CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE application_config (
    id uuid UNIQUE DEFAULT uuid_generate_v4() PRIMARY KEY,
    application_id varchar(255),

    default_signing_algorithm varchar(255),
    default_encryption_algorithm varchar(255),
    default_encryption_method varchar(255),

    key_valid_period bigint,

    current_sign_kid uuid,
    current_enc_kid uuid
);

CREATE TABLE key (
    id uuid UNIQUE DEFAULT uuid_generate_v4() PRIMARY KEY ,
    application_id varchar(255),
    validity_window_stop TIMESTAMP,
    jwk varchar(10000)
);
