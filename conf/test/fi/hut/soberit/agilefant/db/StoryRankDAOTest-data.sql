INSERT INTO backlogs (id, backlogtype, name, rank) VALUES (1, 'Project', 'Test project', 1);

INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (1, 5, 10, 1, 'Project Story 1',null); --root
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (2, 1, 20, 1, 'Project Story 2', null); --middle
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (3, 1, 30, 1, 'Project Story 3', null); -- middle
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (4, 4, 40, 1, 'Project Story 4', NULL); --root, leaf

INSERT INTO storyrank (id, backlog_id, story_id, next_id, previous_id) VALUES (1, 1, 1, null, null);
INSERT INTO storyrank (id, backlog_id, story_id, next_id, previous_id) VALUES (2, 1, 2, null, 1);
INSERT INTO storyrank (id, backlog_id, story_id, next_id, previous_id) VALUES (3, 1, 3, null, 2);
INSERT INTO storyrank (id, backlog_id, story_id, next_id, previous_id) VALUES (4, 1, 4, null, 3);

UPDATE storyrank SET next_id=2 WHERE id=1;
UPDATE storyrank SET next_id=3 WHERE id=2;
UPDATE storyrank SET next_id=4 WHERE id=3;
