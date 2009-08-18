INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Iteration', 'Iteration 1');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Iteration', 'Iteration 2');
INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (1, 1, 'Story 1', 10, 0);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (2, 1, 'Story 2', 5,  0);
INSERT INTO stories (id, backlog_id, name, storypoints, state) VALUES (3, 1, 'Story 3', 5,  0);

/** DATA FOR RECURSIVE CALCULATION OF STORY POINTS **/
INSERT INTO backlogs (id, backlogtype, name) VALUES (3, 'Project', 'Project 1');
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (4, 3, 'Iteration', 'Iteration 4');
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (5, 3, 'Iteration', 'Iteration 4');

-- PROJECT STORY
INSERT INTO stories (id, backlog_id, state, storypoints, name) VALUES (11, 3, 2, 30, 'Project story 1');

-- ITERATION STORIES
INSERT INTO stories (id, backlog_id, state, storypoints, name) VALUES (12, 4, 0, 12, 'Iteration story 2');
INSERT INTO stories (id, backlog_id, state, storypoints, name) VALUES (13, 4, 0, 17, 'Iteration story 3');

INSERT INTO stories (id, backlog_id, state, storypoints, name) VALUES (14, 5, 0, 9, 'Iteration story 4');

