CREATE TABLE task_groups (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    estimation NUMERIC,
    task_group_id INT NOT NULL
);

ALTER TABLE tasks ADD CONSTRAINT fk_task_task_group FOREIGN KEY (task_group_id) REFERENCES task_groups (id);

CREATE TABLE iterations (
    id SERIAL PRIMARY KEY,
    start_at TIMESTAMP WITHOUT TIME ZONE,
    end_at TIMESTAMP WITHOUT TIME ZONE,
    task_group_id INT NOT NULL
);

ALTER TABLE iterations ADD CONSTRAINT fk_iteration_task_group FOREIGN KEY (task_group_id) REFERENCES task_groups (id);