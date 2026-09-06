CREATE TABLE CATEGORY(
  id bigserial primary key,
  name varchar(100) not null unique,
  description text,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp
);
