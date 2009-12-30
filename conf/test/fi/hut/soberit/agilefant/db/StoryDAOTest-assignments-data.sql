INSERT INTO backlogs (id, backlogtype, name, enddate) VALUES (1, 'Iteration', 'Iteration 1', '2100-10-10 00:00:00');

INSERT INTO stories (id, state, backlog_id, name) VALUES (1, 5, 1, 'Story 1');
INSERT INTO stories (id, state, backlog_id, name) VALUES (2, 5, 1, 'Story 2');

INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO story_user (story_id, user_id) VALUES(1, 1);
