INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (1, null, 'Product', 'Product 1');
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (2, null, 'Product', 'Product 2');
INSERT INTO backlogs (id, parent_id, backlogtype, name, rank) VALUES (3, 1, 'Project', 'Project 1', 1);

INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id) VALUES (1, 'BacklogHourEntry', 20, 1, 3);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id) VALUES (2, 'BacklogHourEntry', 30, 1, 3);

