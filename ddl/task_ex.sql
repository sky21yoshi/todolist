CREATE TABLE TASK_EX(
  task_id int primary key references TASK(id) on delete cascade,
  due_date timestamp,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp
);
CREATE INDEX idx_task_ex_due_date ON TASK_EX(due_date);
