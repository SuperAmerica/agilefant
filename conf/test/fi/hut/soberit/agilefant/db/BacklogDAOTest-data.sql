INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Iteration', 'Iteration 1');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Iteration', 'Iteration 2');
INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints) VALUES (1, 1, 'Story 1', 1, 10);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints) VALUES (2, 1, 'Story 2', 1, 5);
INSERT INTO stories (id, backlog_id, name, creator_id, storypoints) VALUES (3, 1, 'Story 3', 1, 5);
