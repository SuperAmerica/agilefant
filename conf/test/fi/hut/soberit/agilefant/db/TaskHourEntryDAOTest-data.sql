/*
 * Create backlog tree, where right nodes have no children and in the left side product and project nodes have 
 * exactly two children.
 */
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (1, null, 'Product', 'Product 1');
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (2, null, 'Product', 'Product 2');
INSERT INTO backlogs (id, parent_id, backlogtype, name, rank) VALUES (3, 1, 'Project', 'Project 1', 1);
INSERT INTO backlogs (id, parent_id, backlogtype, name, rank) VALUES (4, 1, 'Project', 'Project 2', 2);
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (5, 3, 'Iteration', 'Iteration 1');
INSERT INTO backlogs (id, parent_id, backlogtype, name) VALUES (6, 3, 'Iteration', 'Iteration 2');

INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (1, true, 1);
INSERT INTO users (id, enabled, recentItemsNumberOfWeeks) VALUES (2, true, 1);

INSERT INTO stories (id, iteration_id, name, state) VALUES (1, 5, 'Story 1', 0);
INSERT INTO stories (id, iteration_id, name, state) VALUES (2, 5, 'Story 2', 0);
INSERT INTO stories (id, iteration_id, name, state) VALUES (3, 5, 'Story 3', 0);

INSERT INTO stories (id, backlog_id, name, state) VALUES (4, 1, 'Story 4', 0);
INSERT INTO stories (id, backlog_id, name, state) VALUES (5, 1, 'Story 5', 0);

INSERT INTO stories (id, backlog_id, name, state) VALUES (6, 3, 'Story 6', 0);
INSERT INTO stories (id, backlog_id, name, state) VALUES (7, 3, 'Story 7', 0);


INSERT INTO tasks (id, story_id, state) VALUES (1, 1, 0);
INSERT INTO tasks (id, story_id, state) VALUES (2, 1, 0);
INSERT INTO tasks (id, story_id, state) VALUES (3, 1, 0);

INSERT INTO tasks (id, story_id, state) VALUES (4, 4, 0);
INSERT INTO tasks (id, story_id, state) VALUES (5, 4, 0);

INSERT INTO tasks (id, story_id, state) VALUES (6, 6, 0);
INSERT INTO tasks (id, story_id, state) VALUES (7, 6, 0);

INSERT INTO tasks (id, iteration_id, state) VALUES (8, 5, 0);
INSERT INTO tasks (id, iteration_id, state) VALUES (9, 5, 0);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (1, 'TaskHourEntry', 20, 1, 1);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (2, 'TaskHourEntry', 30, 1, 1);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (3, 'TaskHourEntry', 40, 1, 2);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (4, 'TaskHourEntry', 50, 1, 2);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (5, 'TaskHourEntry', 20, 1, 8);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (6, 'TaskHourEntry', 40, 1, 9);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (20, 'TaskHourEntry', 40, 1, 4);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (21, 'TaskHourEntry', 40, 1, 4);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (22, 'TaskHourEntry', 40, 1, 6);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, task_id) VALUES (23, 'TaskHourEntry', 40, 1, 6);




