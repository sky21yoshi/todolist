CREATE TABLE APP_USER (
  id bigserial primary key,
  email varchar(100) not null unique,
  password char(64) not null,
  expires_at timestamp not null,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp
);
