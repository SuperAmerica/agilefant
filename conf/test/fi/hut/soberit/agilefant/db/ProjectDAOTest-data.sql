INSERT INTO users (id,enabled) VALUES (1,true);
INSERT INTO users (id,enabled) VALUES (2,true);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (1,'Project',1);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (2,'Project',2);
INSERT INTO backlogs (id,backlogtype, rank) VALUES (3,'Project',3);



INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (4,'Iteration',4,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (5,'Iteration',5,3);
INSERT INTO backlogs (id,backlogtype, rank, parent_id) VALUES (6,'Iteration',6,3);

INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (1,1,1,100);
INSERT INTO assignment (id, user_id, backlog_id, availability) VALUES (2,1,1,100);

INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (1,3,33333,0, '');
INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (2,4,44444,5, '');
INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (3,5,10,5, '');
INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (4,6,100,5, '');
INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (5,4,1000,0, '');
INSERT INTO stories (id, backlog_id, storyPoints, state, name) VALUES (6,3,10000,5, '');

INSERT INTO storyrank (backlog_id, story_id, rank) VALUES (3,3,0);
INSERT INTO storyrank (backlog_id, story_id, rank) VALUES (3,4,1);
INSERT INTO storyrank (backlog_id, story_id, rank) VALUES (3,5,2);
INSERT INTO storyrank (backlog_id, story_id, rank) VALUES (3,6,3);