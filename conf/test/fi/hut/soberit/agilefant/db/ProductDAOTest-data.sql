INSERT INTO backlogs (id,backlogtype, rank) VALUES (1,'Product',1);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (2,'Product',1);

INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (3,'Project',1,1);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (4,'Project',1,1);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (5,'Project',1,1);

INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (6,'Iteration',1,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (7,'Iteration',1,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (8,'Iteration',1,3);