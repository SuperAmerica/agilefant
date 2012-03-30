--iteration
INSERT INTO backlogs (id,name,backlogtype, startDate) VALUES (1, 'iter', 'Iteration', '2010-01-06 00:00:00');
--stories
INSERT INTO stories (id,name,state,iteration_id) VALUES (1, 'Story 1', 0, 1);
INSERT INTO stories (id,name,state,iteration_id) VALUES (2, 'Story 2', 0, 1);

INSERT INTO tasks (id,name,state,story_id) VALUES (1,'Task in story 1',0,1);
INSERT INTO tasks (id,name,state,iteration_id) VALUES (2,'Task in iteration',0,1);
-- initial audit 

INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (1, 1262649600000, 1); --5.1.2010

INSERT INTO backlogs_AUD (REV,REVTYPE,id,name,backlogtype, startDate) VALUES (1,0,1, 'iter', 'Iteration', '2010-01-01 00:00:00');

INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (1,0, 1, 'Story 1', 0, 1);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (1,0, 2, 'Story 2', 0, 1);

INSERT INTO tasks_AUD (REV,REVTYPE,id,name,state,story_id) VALUES (1,0,1,'Task in story 1',0,1);
INSERT INTO tasks_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (1,0,2,'Task in iteration',0,1);


--add a story
INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (2, 1279095897000, 1); --10.1.2010

INSERT INTO stories (id,name,state,iteration_id) VALUES (3, 'Story 3', 0, 1);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (2,0,3, 'Story 3', 0, 1);

INSERT INTO tasks (id,name,state,story_id) VALUES (3,'Task in story 3',0,3);
INSERT INTO tasks (id,name,state,iteration_id) VALUES (4,'Task 2 in iteration - added',0,1);

INSERT INTO tasks_AUD (REV,REVTYPE,id,name,state,story_id) VALUES (2,0,3,'Task in story 3',0,1);
INSERT INTO tasks_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (2,0,4,'Task 2 in iteration',0,1);





