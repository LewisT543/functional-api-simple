CREATE TABLE IF NOT EXISTS todo (
  id SERIAL PRIMARY KEY,
  description TEXT,
  importance TEXT
);

CREATE TABLE IF NOT EXISTS big_tasks (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description_content TEXT NOT NULL,
  description_last_edited_on TIMESTAMP NOT NULL,
  description_content_length INT NOT NULL,
  status VARCHAR(50) DEFAULT 'pending'
);