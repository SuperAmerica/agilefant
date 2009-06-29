-- Backlogs
INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Product', 'Test product');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Project', 'Test project');
INSERT INTO backlogs (id, backlogtype, name) VALUES (3, 'Iteration', 'Test iteration');


-- DONE items - total story points 25
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (1, 5, 12, 1, 'Story 1');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (2, 5,  4, 1, 'Story 2');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (3, 5,  9, 3, 'Story 3');

-- Not DONE items - total story points 37
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (4, 0,     7, 1, 'Story 4');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (5, 1,     1, 1, 'Story 5');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (6, 2,    18, 3, 'Story 6');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (7, 3,     6, 3, 'Story 7');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (8, 4,     5, 1, 'Story 8');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (9, 4,  null, 1, 'Story 9');

-- Create user --
INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO story_user (story_id, user_id) VALUES(1,1);

-- Tasks for story 3 --
INSERT INTO tasks (id, story_id, state) VALUES (1, 3, 0);

-- Task for story 1 --
INSERT INTO tasks (id, story_id, state) VALUES (2, 1, 0);

-- Hour entries for tasks --
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (1, 'TaskHourEntry', 20, 1, 1);

-- Hour entries for story 2 --
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (13, 'StoryHourEntry', 20, 1, 2);