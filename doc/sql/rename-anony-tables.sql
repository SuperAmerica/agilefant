-- Rename anonymous tables back to original
-- Its a temporary solution, Todo: write a script to go information_schema.tables and rename it
-- Used the following statement to create this file 



select concat ('RENAME TABLE agilefant.anonym_', table_name, ' TO agilefant.', table_name, ';') FROM information_schema.tables WHERE table_schema = 'agilefant' INTO OUTFILE 'tmpanonyme.sql';
use agilefant;
source tmpanonyme.sql; 



/*
select * from show tables from 'agilefant' as t2;
ALTER TABLE t1 to RENAME t2 

RENAME TABLE t1 to t2
From information_schema.tables AS t2, information_schema.tables AS t1
WHERE t2.table_schema = 'agilefant' AND t1.table_schema = 'agilefant' AND t2 LIKE 'agilefant.anonym_%'+t1;
*/


/*
RENAME TABLE agilefant.anonym_agilefant_revisions TO agilefant.agilefant_revisions;

RENAME TABLE agilefant.anonym_assignment TO agilefant.assignment;

RENAME TABLE agilefant.anonym_assignment_aud TO agilefant.assignment_aud;

RENAME TABLE agilefant.anonym_backlogs TO agilefant.backlogs;

RENAME TABLE agilefant.anonym_backlogs_aud TO agilefant.backlogs_aud;

RENAME TABLE agilefant.anonym_history_backlogs TO agilefant.history_backlogs;

RENAME TABLE agilefant.anonym_history_iterations TO agilefant.history_iterations;

RENAME TABLE agilefant.anonym_holiday TO agilefant.holiday;

RENAME TABLE agilefant.anonym_holidayanomaly TO agilefant.holidayanomaly;

RENAME TABLE agilefant.anonym_hourentries TO agilefant.hourentries;

RENAME TABLE agilefant.anonym_labels TO agilefant.labels;

RENAME TABLE agilefant.anonym_settings TO agilefant.settings;

RENAME TABLE agilefant.anonym_stories TO agilefant.stories;

RENAME TABLE agilefant.anonym_stories_aud TO agilefant.stories_aud;

RENAME TABLE agilefant.anonym_story_access TO agilefant.story_access;

RENAME TABLE agilefant.anonym_story_user TO agilefant.story_user;

RENAME TABLE agilefant.anonym_story_user_aud TO agilefant.story_user_aud;

RENAME TABLE agilefant.anonym_storyrank TO agilefant.storyrank;

RENAME TABLE agilefant.anonym_storyrank_aud TO agilefant.storyrank_aud;

RENAME TABLE agilefant.anonym_task_user TO agilefant.task_user;

RENAME TABLE agilefant.anonym_task_user_aud TO agilefant.task_user_aud;

RENAME TABLE agilefant.anonym_tasks TO agilefant.tasks;

RENAME TABLE agilefant.anonym_tasks_aud TO agilefant.tasks_aud;

RENAME TABLE agilefant.anonym_team_user TO agilefant.team_user;

RENAME TABLE agilefant.anonym_teams TO agilefant.teams;

RENAME TABLE agilefant.anonym_users TO agilefant.users;

RENAME TABLE agilefant.anonym_users_aud TO agilefant.users_aud;

RENAME TABLE agilefant.anonym_whatsnextentry TO agilefant.whatsnextentry;

RENAME TABLE agilefant.anonym_widgetcollections TO agilefant.widgetcollections;

RENAME TABLE agilefant.anonym_widgets TO agilefant.widgets;

RENAME TABLE agilefant.anonym_team_product TO agilefant.team_product;
*/