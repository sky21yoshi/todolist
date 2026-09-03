CREATE TABLE USER_GROUP (
  user_id bigint not null references APP_USER(id) on delete cascade,
  group_id bigint not null references APP_GROUP(id) on delete cascade,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key (user_id, group_id)
);
CREATE INDEX idx_user_group_user_id ON USER_GROUP(user_id);
CREATE INDEX idx_user_group_group_id ON USER_GROUP(group_id);
