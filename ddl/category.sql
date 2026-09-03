CREATE TABLE CATEGORY(
  id serial primary key,
  name varchar(100) not null unique,
  description text,
  created_at timestamp default current_timestamp default current_timestamp,
  updated_at timestamp default current_timestamp default current_timestamp
);
