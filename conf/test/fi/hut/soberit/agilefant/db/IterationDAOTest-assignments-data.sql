INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);

INSERT INTO backlogs (id,backlogtype, rank, enddate) VALUES (1,'Iteration',1, '2100-12-31 00:00:00');
INSERT INTO backlogs (id,backlogtype, rank, enddate) VALUES (2,'Iteration',2, '2100-12-31 00:00:00');
INSERT INTO backlogs (id,backlogtype, rank) VALUES (3,'Iteration',3);

INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (1,1,1,100);
INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (2,1,1,100);
