CREATE TABLE TASK_TAG (
  task_id bigint not null references TASK(id) on delete cascade,
  tag_id int not null references TAG(id) on delete cascade,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key (task_id, tag_id)
);
CREATE INDEX idx_task_tag_task_id ON TASK_TAG(task_id);
CREATE INDEX idx_task_tag_tag_id ON TASK_TAG(tag_id);
