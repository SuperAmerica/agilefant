
-- Remove duplicate entries
CREATE TABLE temp_table AS SELECT * FROM task_user GROUP BY tasks_id, responsibles_id;
DROP TABLE task_user;
RENAME TABLE temp_table TO task_user;

ALTER TABLE task_user ADD INDEX FKAC91A45B1C109E9 (tasks_id), ADD CONSTRAINT FKAC91A45B1C109E9 foreign key (tasks_id) REFERENCES tasks (id);
ALTER TABLE task_user ADD INDEX FKAC91A4527F4B120 (responsibles_id), ADD CONSTRAINT FKAC91A4527F4B120 foreign key (responsibles_id) REFERENCES users (id);
