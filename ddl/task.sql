CREATE TABLE TASK(
  id serial primary key,
  title varchar(255) not null,
  description text,
  display_order integer not null default 0,
  priority integer not null default 0,
  completed boolean not null default false,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  CHECK (priority >= 0 AND priority <= 5),
  CHECK (display_order >= 0)
);
