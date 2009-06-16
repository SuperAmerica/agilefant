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

INSERT INTO users (id, enabled) VALUES (1, true);
INSERT INTO users (id, enabled) VALUES (2, true);

INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (1, 5, 'Story 1', 1, 0);
INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (2, 5, 'Story 2', 1, 0);
INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (3, 5, 'Story 3', 1, 0);

INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (4, 1, 'Story 4', 1, 0);
INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (5, 1, 'Story 5', 1, 0);

INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (6, 3, 'Story 6', 1, 0);
INSERT INTO stories (id, backlog_id, name, creator_id, state) VALUES (7, 3, 'Story 7', 1, 0);


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


INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (7, 'BacklogHourEntry', 20, 1, 1, '2009-05-10 10:20:00');
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (8, 'BacklogHourEntry', 50, 1, 1, '2009-05-11 10:20:00');
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (9, 'BacklogHourEntry', 100, 1, 3, '2009-05-12 10:20:00');
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (10, 'BacklogHourEntry', 400, 1, 3, '2009-05-13 10:20:00');
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (11, 'BacklogHourEntry', 1000, 1, 5, '2009-05-14 10:20:00');
INSERT INTO hourentries (id, dtype, minutesspent, user_id, backlog_id, `date`) VALUES (12, 'BacklogHourEntry', 3000, 1, 5, '2009-05-15 10:20:00');



INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (13, 'StoryHourEntry', 20, 1, 1);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (14, 'StoryHourEntry', 20, 1, 1);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (15, 'StoryHourEntry', 20, 1, 4);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (16, 'StoryHourEntry', 20, 1, 4);

INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (17, 'StoryHourEntry', 20, 1, 6);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (18, 'StoryHourEntry', 20, 1, 6);
INSERT INTO hourentries (id, dtype, minutesspent, user_id, story_id) VALUES (19, 'StoryHourEntry', 20, 1, 6);



