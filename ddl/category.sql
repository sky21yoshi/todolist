CREATE TABLE CATEGORY(
  id serial,
  name char(100) not null,
  description text,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp
);
