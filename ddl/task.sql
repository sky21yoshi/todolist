CREATE TABLE TASK(
  id serial,
  title char(255) not null,
  description text,
  order integer not null default 0,
  priority integer not null default 0,
  completed boolean not null default false,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp
);
