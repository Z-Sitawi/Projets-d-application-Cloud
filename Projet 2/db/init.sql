CREATE TABLE IF NOT EXISTS students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO students (name, email)
VALUES
  ('Zakaria', 'zakaria@usmba.ac.ma'),
  ('Imad', 'imad@usmba.ac.ma'),
  ('zak', 'zak@usmba.ac.ma'),
  ('Alice', 'alice@usmba.ac.ma'),
  ('Bob', 'bob@usmba.ac.ma');
