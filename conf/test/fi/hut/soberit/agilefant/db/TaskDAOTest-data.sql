INSERT INTO users (id,enabled) VALUES (1,true);
INSERT INTO users (id,enabled) VALUES (2,true);

INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (1,'Iteration','2009-05-20 10:15:00', '2009-07-01 10:15:00');

INSERT INTO stories (id,backlog_id, name, state) VALUES (1,1, 'Story', 1);

INSERT INTO tasks (id,state, effortLeft, iteration_id) VALUES (1,1,100,1);
INSERT INTO tasks (id,state, effortLeft, iteration_id)  VALUES (2,1,400,1);
INSERT INTO tasks (id,state, effortLeft, iteration_id)  VALUES (7,1,800,1);

INSERT INTO tasks (id,state, effortLeft, story_id, iteration_id) VALUES (3,1,4000,1,1);
INSERT INTO tasks (id,state, effortLeft, story_id) VALUES (4,1,1000,1);
INSERT INTO tasks (id,state, effortLeft, story_id) VALUES (5,1,1000,1);
INSERT INTO tasks (id,state, effortLeft, story_id) VALUES (6,1,3000,1);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(1,1);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(2,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(2,2);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(3,1);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(4,1);
INSERT INTO task_user (tasks_id, responsibles_id) VALUES(4,2);

INSERT INTO task_user (tasks_id, responsibles_id) VALUES(5,1);

INSERT INTO story_user (story_id, user_id) VALUES(1,1);