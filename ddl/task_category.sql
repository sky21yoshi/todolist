CREATE TABLE TASK_CATEGORY(
  task_id int not null references TASK(id) on delete cascade,
  category_id int not null references CATEGORY(id) on delete cascade,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key (task_id, category_id)
);
CREATE INDEX idx_task_category_task_id ON TASK_CATEGORY(task_id);
CREATE INDEX idx_task_category_category_id ON TASK_CATEGORY(category_id);
