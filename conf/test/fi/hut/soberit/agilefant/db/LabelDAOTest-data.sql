INSERT INTO backlogs (id,backlogtype,startDate, endDate) VALUES (1,'Iteration','2009-06-01 10:15:00', '2009-06-10 10:15:00');

INSERT INTO stories (id,state, backlog_id, name) VALUES (1,1,1, 'TestStory');

INSERT INTO labels (id, displayName, name, story_id) VALUES (1, 'Kissa', 'kissa', 1);