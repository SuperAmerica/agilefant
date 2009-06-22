INSERT INTO users (id,enabled) VALUES (1,true);
INSERT INTO users (id,enabled) VALUES (2,true);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (1,'Project',1);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (2,'Project',2);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (3,'Project',3);
INSERT INTO assignment (id, user_id, project_id) VALUES (1,1,1);
INSERT INTO assignment (id, user_id, project_id) VALUES (2,1,1);