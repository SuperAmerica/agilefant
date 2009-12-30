INSERT INTO backlogs (id, backlogtype, name, backlogSize, startDate, endDate) VALUES (1, 'Iteration', 'Iteration 1', 400, '2009-05-20 10:15:00', '2009-07-01 10:15:00');
INSERT INTO backlogs (id, backlogtype, name, backlogSize, startDate, endDate) VALUES (2, 'Iteration', 'Iteration 2', 400, '2009-05-20 10:15:00', '2009-07-01 10:15:00');
INSERT INTO backlogs (id, backlogtype, name, startDate, endDate) VALUES (3, 'Iteration', 'Iteration 3', '2009-05-20 10:15:00', '2009-08-01 10:15:00');
INSERT INTO backlogs (id, backlogtype, name, backlogSize, startDate, endDate) VALUES (4, 'Iteration', 'Iteration 4', 400, '2009-05-20 10:15:00', '2009-07-01 10:15:00');

INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (1, 1, 'Story 1', 10, 0);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (2, 1, 'Story 2', 5,  5);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (3, 2, 'Story 3', 5,  0);

INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (4, 3, 'Story 3', 5,  1);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (5, 3, 'Story 4', 5,  2);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (6, 3, 'Story 5', 5,  2);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (7, 3, 'Story 6', 5,  3);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (8, 3, 'Story 7', 5,  3);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (9, 3, 'Story 8', 5,  3);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (10, 3, 'Story 9', 5,  4);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (11, 3, 'Story 10', 5,  4);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (12, 3, 'Story 11', 5,  4);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (13, 3, 'Story 12', 5,  4);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (14, 3, 'Story 13', 5,  5);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (15, 3, 'Story 14', 5,  5);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (16, 3, 'Story 15', 5,  5);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (17, 3, 'Story 16', 5,  5);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (18, 3, 'Story 17', 5,  5);

INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (1, 1, NULL, 1);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (2, 5, NULL, 1);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (3, 5, 1, NULL);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (4, 1, NULL, 2);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (5, 5, NULL, 3);

INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,1,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,2,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,3,100);
INSERT INTO assignment (user_id, backlog_id, availability) VALUES (1,4,100);