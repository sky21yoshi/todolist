-- Create TASK table
CREATE TABLE IF NOT EXISTS TASK (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  "order" INTEGER NOT NULL DEFAULT 0,
  priority INTEGER NOT NULL DEFAULT 0,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create CATEGORY table
CREATE TABLE IF NOT EXISTS CATEGORY (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create TAG table
CREATE TABLE IF NOT EXISTS TAG (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create TASK_EX table (extended task properties)
CREATE TABLE IF NOT EXISTS TASK_EX (
  task_id INTEGER PRIMARY KEY REFERENCES TASK(id) ON DELETE CASCADE,
  due_date TIMESTAMP,
  category_id INTEGER REFERENCES CATEGORY(id) ON DELETE SET NULL,
  tag_id INTEGER REFERENCES TAG(id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_task_completed ON TASK(completed);
CREATE INDEX IF NOT EXISTS idx_task_created_at ON TASK(created_at);
CREATE INDEX IF NOT EXISTS idx_task_priority ON TASK(priority);
CREATE INDEX IF NOT EXISTS idx_task_ex_category_id ON TASK_EX(category_id);
CREATE INDEX IF NOT EXISTS idx_task_ex_tag_id ON TASK_EX(tag_id);

-- Insert sample data (optional)
INSERT INTO CATEGORY (name, description) VALUES 
  ('Work', 'Work-related tasks'),
  ('Personal', 'Personal tasks'),
  ('Shopping', 'Shopping tasks')
ON CONFLICT DO NOTHING;

INSERT INTO TAG (name, description) VALUES 
  ('Urgent', 'Urgent tasks'),
  ('Important', 'Important tasks'),
  ('Review', 'Tasks to review')
ON CONFLICT DO NOTHING;
