-- Backlogs
INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Product', 'Test product');
INSERT INTO backlogs (id, backlogtype, name) VALUES (2, 'Project', 'Test project');
INSERT INTO backlogs (id, backlogtype, name) VALUES (3, 'Iteration', 'Test iteration');


-- DONE items - total story points 25
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (1, 5, 12, 1, 'Story 1');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (2, 5,  4, 1, 'Story 2');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (3, 5,  9, 3, 'Story 3');

-- Not DONE items - total story points 37
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (4, 0,  7, 1, 'Story 4');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (5, 1,  1, 1, 'Story 5');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (6, 2, 18, 3, 'Story 6');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (7, 3,  6, 3, 'Story 7');
INSERT INTO stories (id, state, storyPoints, backlog_id, name) VALUES (8, 4,  5, 1, 'Story 8');
