INSERT INTO users (id,enabled) VALUES (1,true);
INSERT INTO users (id,enabled) VALUES (2,true);

INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (1,'Iteration','2009-05-20 10:15:00', '2009-07-01 10:15:00');
INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (2,'Project','2009-05-20 10:15:00', '2009-07-01 10:15:00');

INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,1,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (2,1,100);

INSERT INTO stories (id,backlog_id, name, state) VALUES (1,1, 'Story', 1);
INSERT INTO stories (id,backlog_id, name, state) VALUES (2,2, 'Story 2', 1);
INSERT INTO stories (id,backlog_id, name, state) VALUES (3,1, 'Story 3', 1);

-- 2 tasks directly under iteration 1
-- 1 assignee - done already
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank) VALUES (1,5,100,1,0);
-- 2 assignees
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank)  VALUES (2,1,400,1,1);
-- 4 tasks attached to story 1
-- 1 assignee
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (3,1,4000,1,0);
-- 2 assignees
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (4,1,1000,1,1);
-- 1 assignee - done already
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (5,5,1000,1,2);
-- 1 assignee through story
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (6,1,3000,1,3);
-- no assignees
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank)  VALUES (7,1,800,1,10);
-- 2 tasks for story 2 (project story)
-- 1 assignee
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (8,1,10000,2,3);
-- 1 assignee - done already
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (9,5,10000,2,4);
-- one task without a parent
INSERT INTO tasks (id,state, effortLeft, rank) VALUES (10,1,10000,0);
-- tasks for story 3
-- no assignees
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (11,1,10000,3,0);
-- no assignees
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (12,1,50000,3,1);
-- iteration tasks without assignees
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank) VALUES (13,1,50,1,2);
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank) VALUES (14,1,120,1,3);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(1,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(2,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(2,2);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(3,1);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(4,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(4,2);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(5,1);

INSERT INTO story_user (story_id, user_id) VALUES(1,1);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(8,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(9,1);

--RANKS
INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (3,'Iteration','2009-05-20 10:15:00', '2009-07-01 10:15:00');

INSERT INTO stories (id,backlog_id, name, state) VALUES (55,3, 'Story', 1);

INSERT INTO tasks (id,state, effortLeft, iteration_id, rank) VALUES (16,1,10000,3,5);
INSERT INTO tasks (id,state, effortLeft, iteration_id, rank) VALUES (17,1,10000,3,1500);

INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (20,1,10000,55,0);
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (21,1,10000,55,1);
INSERT INTO tasks (id,state, effortLeft, story_id, rank) VALUES (22,1,10000,55,666);


