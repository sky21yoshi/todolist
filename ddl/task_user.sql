CREATE TABLE TASK_USER(
  task_id int not null references TASK(id) on delete cascade,
  user_id int not null references APP_USER(id) on delete cascade,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key (task_id, user_id)
);
CREATE INDEX idx_task_user_task_id ON TASK_USER(task_id);
CREATE INDEX idx_task_user_user_id ON TASK_USER(user_id);
