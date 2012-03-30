--iteration
INSERT INTO backlogs (id,name,backlogtype) VALUES (1, 'iter', 'Iteration');
--stories
INSERT INTO stories (id,name,state,iteration_id) VALUES (1, 'Story 1', 0, 1);
INSERT INTO stories (id,name,state,iteration_id) VALUES (2, 'Story 2', 0, 1);

INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (1, 1279095869, 1); --1.1.2010

INSERT INTO backlogs_AUD (REV,REVTYPE,id,name,backlogtype) VALUES (1,0,1, 'iter', 'Iteration');

INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (1,0, 1, 'Story 1', 0, 1);
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (1,0, 2, 'Story 2', 0, 1);

UPDATE stories SET state=2 WHERE id=1;
INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (2, 1279095897, 1); --10.1.2010
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (2,1, 1, 'Story 1', 2, 1);


UPDATE stories SET state=4 WHERE id=1;
INSERT INTO agilefant_revisions (id,`timestamp`, userId) VALUES (3, 1279095911, 1); --20.1.2010
INSERT INTO stories_AUD (REV,REVTYPE,id,name,state,iteration_id) VALUES (3,1, 1, 'Story 1', 4, 1);