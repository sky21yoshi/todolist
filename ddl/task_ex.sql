CREATE TABLE TASK_EX(
  task_id serial foreign key references TASK(id),
  due_date timestamp,
  category_id serial foreign key references CATEGORY(id),
  tag_id serial foreign key references TAG(id),
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp
);
