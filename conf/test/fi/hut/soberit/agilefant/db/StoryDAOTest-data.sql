-- Backlogs
INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Product', 'Test product');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Project', 'Test project');
INSERT INTO backlogs (id, backlogtype, name, startDate, endDate) VALUES (3, 'Iteration', 'Test iteration', '2009-05-20 10:15:00', '2009-07-01 10:15:00');
INSERT INTO backlogs (id, backlogtype, name) VALUES (4, 'Iteration', 'Ranking parent iteration');
INSERT INTO backlogs (id, backlogtype, name) VALUES (5, 'Project', 'Ranking parent project with no stories');
INSERT INTO backlogs (id, backlogtype, name, startDate, endDate,parent_id) VALUES (6, 'Iteration', 'Standalone iteration', '2009-05-20 10:15:00', '2009-07-01 10:15:00',null);

-- DONE items - total story points 25
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (1, 5, 12, 1, 'Story 1');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (2, 5,  4, 1, 'Story 2');
INSERT INTO stories (id, state, storyPoints, iteration_id, name) VALUES (3, 5,  9, 3, 'Story 3');

-- Not DONE items - total story points 37
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (4, 0,     7, 1, 'Story 4');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (5, 1,     1, 1, 'Story 5');
INSERT INTO stories (id, state, storyPoints, iteration_id, name) VALUES (6, 2,    18, 3, 'Story 6');
INSERT INTO stories (id, state, storyPoints, iteration_id, name) VALUES (7, 3,     6, 3, 'Story 7');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (8, 4,     5, 1, 'Story 8');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (9, 4,  null, 1, 'Story 9');
INSERT INTO stories (id, state, storyPoints, iteration_id, name) VALUES (10, 3,   26, 6, 'Story 10');

-- Create user --
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);
INSERT INTO story_user (story_id, user_id) VALUES(1, 1); -- in product --
INSERT INTO story_user (story_id, user_id) VALUES(6, 1); -- in iteration 3 --
INSERT INTO story_user (story_id, user_id) VALUES(7, 1); -- in iteration 3 --
INSERT INTO story_user (story_id, user_id) VALUES(10, 2); -- in standalone iteration 6 --
INSERT INTO story_user (story_id, user_id) VALUES(6, 2); -- in standalone iteration 6 --
--- INSERT INTO story_user (story_id, user_id) VALUES(24,1); -- in iteration 4, not in timebox --

-- Tasks for story 3 --
INSERT INTO tasks (id, story_id, state) VALUES (1, 3, 0);

-- Task for story 1 --
INSERT INTO tasks (id, story_id, state) VALUES (2, 1, 0);

-- Hour entries for tasks --
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (1, 'TaskHourEntry', 20, 1, 1);

-- Hour entries for story 2 --
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (13, 'StoryHourEntry', 20, 1, 2);


-- Story metrics test data
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (100, 4,  null, 1, 'Story 10');

INSERT INTO tasks (id, story_id, state, originalestimate, effortLeft) VALUES (100, 100, 1, 40, 30);
INSERT INTO tasks (id, story_id, state, originalestimate, effortLeft) VALUES (101, 100, 0, 120, 120);
INSERT INTO tasks (id, story_id, state, originalestimate, effortLeft) VALUES (102, 100, 5, 240, 0);

