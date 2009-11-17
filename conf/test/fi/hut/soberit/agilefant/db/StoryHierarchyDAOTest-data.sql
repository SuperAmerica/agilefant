/*
 *           11            
 *           21       33    24
 *         31  22
 *              23 
 *               32
 */
-- product stories 
INSERT INTO backlogs (id, backlogtype, name) VALUES (11, 'Product', 'Test product 2');
INSERT INTO backlogs (id, backlogtype, name) VALUES (1, 'Product', 'Test product');

INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (11, 1, 1, 10, 1, 'Product Story 1', NULL);
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (12, 1, 1, 10, 1, 'Product Story 1', 11);
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (13, 1, 1, 10, 1, 'Product Story 1', 12);
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (14, 1, 1, 10, 1, 'Product Story 1', NULL);

-- release stories
INSERT INTO backlogs (id, backlogtype, name, parent_id, rank) VALUES (2, 'Project', 'Test project', 1, 1);
-- root stories
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (21, 5, 1, 10, 2, 'Project Story 1', 11); --root
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (22, 1, 2, 20, 2, 'Project Story 2', 21); --middle
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (23, 1, 3, 30, 2, 'Project Story 3', 22); -- middle
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (24, 4, 4, 40, 2, 'Project Story 4', NULL); --root, leaf

INSERT INTO backlogs (id, backlogtype, name, rank, parent_id) VALUES (4, 'Project', 'Test project', 2, 1);
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (41, 1, 1, 1000, 4, 'Project Story 1', NULL);

INSERT INTO backlogs (id, backlogtype, name, rank, parent_id) VALUES (5, 'Project', 'Test project', 2, 1);

-- iteration stories
INSERT INTO backlogs (id, backlogtype, name, parent_id) VALUES (3, 'Iteration', 'Test iteration', 2);
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (31, 1, 1, 10, 3, 'Iteration Story 1', 21); --leaf
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (32, 1, 2, 20, 3, 'Iteration Story 2', 23); --leaf
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (33, 5, 3, 30, 3, 'Iteration Story 3', NULL); --root, leaf
INSERT INTO stories (id, state, rank, storyPoints, backlog_id, name, parent_id) VALUES (34, 1, 1, 40, 3, 'Iteration Story 4', 11); --root, leaf

