INSERT INTO backlogs (id,backlogtype, rank) VALUES (1,'Product',1);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (2,'Product',1);

INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (3,'Project',1,1);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (4,'Project',1,1);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (5,'Project',1,1);

INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (6,'Iteration',1,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (7,'Iteration',1,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (8,'Iteration',1,3);

INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (1,'',0,1,null);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (2,'',0,3,1);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (3,'',0,3,null);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (4,'',0,6,null);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (5,'',0,6,null);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (6,'',0,6,null);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (7,'',0,3,3);
INSERT INTO stories (id,name,state,backlog_id,parent_id) VALUES (8,'',0,1,3);