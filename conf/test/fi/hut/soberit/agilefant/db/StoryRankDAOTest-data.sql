INSERT INTO backlogs (id, backlogtype, name, rank) VALUES (1, 'Project', 'Test project', 1);

INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (1, 5, 10, 1, 'Project Story 1',null); 
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (2, 1, 20, 1, 'Project Story 2', null); 
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (3, 1, 30, 1, 'Project Story 3', null);
INSERT INTO stories (id, state, storyPoints, backlog_id, name, parent_id) VALUES (4, 4, 40, 1, 'Project Story 4', NULL); 

INSERT INTO storyrank (id, backlog_id, story_id,rank) VALUES (1, 1, 1,0);
INSERT INTO storyrank (id, backlog_id, story_id,rank) VALUES (2, 1, 2,1);
INSERT INTO storyrank (id, backlog_id, story_id,rank) VALUES (3, 1, 3,2);
INSERT INTO storyrank (id, backlog_id, story_id,rank) VALUES (4, 1, 4,3);

