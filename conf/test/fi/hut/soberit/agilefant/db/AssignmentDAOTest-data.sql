INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (1,'Iteration','2009-06-01 10:15:00', '2009-06-10 10:15:00');
INSERT INTO backlogs (id,backlogtype,startDate, endDate, rank) VALUES (2,'Project','2009-06-01 10:15:00', '2009-06-10 10:15:00',1);

INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);

INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,1,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (2,1,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (2,2,100);
