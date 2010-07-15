-- initial state (rev 1) iteration with 2 stories, no tasks
--iteration
INSERT INTO backlogs (id,name,backlogtype) VALUES (1, 'iter', 'Iteration');
--stories
INSERT INTO stories (id,name,state,backlog_id) VALUES (1, 'Story 1', 0, 1);
INSERT INTO stories (id,name,state,backlog_id) VALUES (2, 'Story 2', 0, 1);
INSERT INTO stories (id,name,state,backlog_id) VALUES (3, 'Story 3', 0, 1);
--story ranks
INSERT INTO storyrank (id, story_id, backlog_id, rank) VALUES (1,1,1,0);
INSERT INTO storyrank (id, story_id, backlog_id, rank) VALUES (2,2,1,1);
INSERT INTO storyrank (id, story_id, backlog_id, rank) VALUES (3,3,1,2);


-- initial audit 

INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (1, 1279095869, 1); --1.1.2010

INSERT INTO backlogs_AUD (REV,REVTYPE,id,name,backlogtype) VALUES (1,0,1, 'iter', 'Iteration');

INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,backlog_id) VALUES (1,0, 1, 'Story 1', 0, 1);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,backlog_id) VALUES (1,0, 2, 'Story 2', 0, 1);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,backlog_id) VALUES (1,0, 3, 'Story 3', 0, 1);

INSERT INTO storyrank_AUD (REV,REVTYPE,id,story_id,backlog_id,rank) VALUES (1,0,1,1,1,0);
INSERT INTO storyrank_AUD (REV,REVTYPE,id,story_id,backlog_id,rank) VALUES (1,0,2,2,1,1);
INSERT INTO storyrank_AUD (REV,REVTYPE,id,story_id,backlog_id,rank) VALUES (1,0,3,3,1,2);

--add a story
INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (2, 1279095897, 1); --10.1.2010

INSERT INTO stories (id,name,state,backlog_id) VALUES (4, 'Story 4', 0, 1);
INSERT INTO storyrank (id, story_id, backlog_id, rank) VALUES (4,4,1,3);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,backlog_id) VALUES (2,0,4, 'Story 4', 0, 1);
INSERT INTO storyrank_AUD (REV,REVTYPE,id,story_id,backlog_id,rank) VALUES (2,0,4,4,1,3);

--remove a story
INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (3, 1279095911, 1); --20.1.2010

INSERT INTO storyrank_AUD (REV,REVTYPE,id,story_id,backlog_id,rank) VALUES (3,2,4,4,1,3);



