INSERT INTO users (id,enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id,enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);

INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (1,'Project',0, '2010-01-20 10:15:00', '2010-02-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (2,'Project',-1, '2009-11-20 10:15:00', '2009-12-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (3,'Project',1, '2009-01-20 10:15:00', '2009-12-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (4,'Project',2, '2009-11-20 10:15:00', '2009-12-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (5,'Project',3, '2009-11-20 10:15:00', '2010-12-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (6,'Project',4, '2009-01-20 10:15:00', '2010-12-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (7,'Project',5, '2009-01-20 10:15:00', '2009-02-20 10:15:00');
INSERT INTO backlogs (id,backlogtype, rank, startDate, endDate) VALUES (8,'Project',6, '2010-01-20 10:15:00', '2010-02-20 10:15:00');

INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (1,1,1,100);
INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (2,1,1,100);
