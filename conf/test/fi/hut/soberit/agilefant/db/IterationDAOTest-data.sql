INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Iteration', 'Iteration 1');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Iteration', 'Iteration 2');
INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints, state) VALUES (1, 1, 'Story 1', 1, 10, 0);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints, state) VALUES (2, 1, 'Story 2', 1, 5,  5);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints, state) VALUES (3, 2, 'Story 3', 1, 5,  0);

INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (1, 1, NULL, 1);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (2, 5, NULL, 1);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (3, 5, 1, NULL);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (4, 1, NULL, 2);
INSERT INTO tasks (id, state, iteration_id, story_id) VALUES (5, 5, NULL, 3);
