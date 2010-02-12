/* If this is not set we can only create deterministic functions */
/* SET GLOBAL log_bin_trust_function_creators = 1; */

SET autocommit = 0;
SET SESSION SQL_MODE='TRADITIONAL';

SET @SCHEMA_NAME = 'agilefant';

delimiter //

CREATE PROCEDURE ExecDyn(IN sqlstr VARCHAR(1000))
BEGIN
  SET @sql = sqlstr;
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END //

CREATE PROCEDURE DropFK(IN table_name VARCHAR(100), IN key_name VARCHAR(100))
  NOT DETERMINISTIC
  MODIFIES SQL DATA
  BEGIN
    Call ExecDyn(CONCAT('ALTER TABLE ', table_name,' DROP FOREIGN KEY ', key_name));
    Call ExecDyn(CONCAT('ALTER TABLE ', table_name,' DROP KEY ', key_name));
  END //

CREATE PROCEDURE DropAllForeignKeys()
BEGIN
  DECLARE done BOOL DEFAULT FALSE;
  DECLARE table_str VARCHAR(64);
  DECLARE constraint_str VARCHAR(64);
  DECLARE cur
    CURSOR FOR
    SELECT DISTINCT table_name,constraint_name
    FROM information_schema.key_column_usage
    WHERE referenced_table_name IS NOT NULL
      AND table_schema=@SCHEMA_NAME;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = TRUE;
  OPEN cur;

  myLoop: LOOP
    FETCH cur INTO table_str,constraint_str;
    IF done then
      CLOSE cur;
      LEAVE myLoop;
    END IF;
    CALL DropFK(table_str,constraint_str);
  END LOOP;
END //

SELECT 'Drop All Foreign Keys' AS status //
CALL DropAllForeignKeys() //

delimiter ;
  
SELECT 'Schema changes' AS status;

/*** ASSIGNMENT ***/
/*
ALTER TABLE assignment
  CHANGE backlog_id project_id INT(11) DEFAULT NULL;
*/
ALTER TABLE assignment
  CHANGE deltaOverhead delta_personal_load BIGINT(20) DEFAULT NULL;
ALTER TABLE assignment
    ADD COLUMN availability INTEGER DEFAULT 100 NOT NULL;

/* convert from seconds to minutes */
UPDATE assignment SET delta_personal_load = (delta_personal_load / 60);

/*** BACKLOGS ***/

ALTER TABLE backlog RENAME backlogs;

ALTER TABLE backlogs DROP COLUMN assignee_id;
ALTER TABLE backlogs DROP COLUMN owner_id;

ALTER TABLE backlogs
  CHANGE project_id parent_id INT(11);

ALTER TABLE backlogs
  CHANGE defaultOverhead baselineLoad BIGINT(20) DEFAULT NULL;

ALTER TABLE backlogs
  CHANGE backlogSize backlogSize BIGINT(20) DEFAULT NULL;
  
UPDATE backlogs SET baselineLoad = (baselineLoad/60) WHERE baselineLoad IS NOT NULL;
UPDATE backlogs SET backlogSize = (backlogSize*60) WHERE backlogSize IS NOT NULL;
  
UPDATE backlogs
  SET parent_id=product_id
  WHERE backlogtype = 'Project';

ALTER TABLE backlogs
  MODIFY rank INT(11);

ALTER TABLE backlogs DROP COLUMN product_id;

/*** TODOS ***/

/* task table contains TODOs.
   They are moved to descriptions of stories and tasks
   */

/* Temporary table because mysql can't use the table to be
   updated in sub querys for where statements */
DROP TABLE IF EXISTS items_with_todos;

/* find out items with todos */
CREATE TEMPORARY TABLE items_with_todos (
  id INT(11)
)
SELECT DISTINCT bi.id
FROM backlogitem AS bi
INNER JOIN task AS t
ON t.backlogItem_id = bi.id;

/* how long items can we handle */
SET SESSION group_concat_max_len = 100000;

/* Insert TODOs as list items into the descriptions */
UPDATE backlogitem
SET description=CONCAT(description,'<ul>',
  ( SELECT CONCAT('<li>',
      GROUP_CONCAT(name ORDER BY priority DESC
                 SEPARATOR '</li><li>'),
          '</li>')
    FROM task
    WHERE task.backlogItem_id = backlogitem.id
    GROUP BY backlogItem_id
  ),'</ul>')
WHERE id in (SELECT id FROM items_with_todos);

DROP TABLE task;

/*** SPLITTING BACKLOG ITEMS ***/

SELECT 'Split backlog items to new tables' AS status;

CREATE TABLE tasks (
  id INT(11) AUTO_INCREMENT,
  effortLeft BIGINT(20),
  originalestimate BIGINT(20),
  name VARCHAR(255),
  priority INT(11),
  state INT(11) NOT NULL,
  rank INT DEFAULT 0,
  description text,
  iteration_id INT(11),
  story_id INT(11),
  PRIMARY KEY(id)
) ENGINE=InnoDB;

INSERT INTO tasks (id,effortLeft,originalestimate,name,
                   priority,state,description,iteration_id,story_id)
  SELECT item.id, item.effortLeft/60, item.originalEstimate/60,
         item.name, item.priority, item.state, item.description, item.backlog_id,
       item.iterationGoal_id
  FROM backlogitem AS item
  INNER JOIN backlogs bl
  ON item.backlog_id = bl.id
  WHERE bl.backlogtype = 'Iteration';

/* We have stuff coming here from both backlogitem
   and iterationgoal */
CREATE TABLE stories (
  id INT(11) AUTO_INCREMENT,
  storyPoints INT(11),
  name VARCHAR(255) NOT NULL,
  priority INT(11),
  state INT(11) NOT NULL,
  description text,
  rank INTEGER DEFAULT 0,
  backlog_id INT(11) NOT NULL,
  iterationGoal_id INT(11), /* temporary */
  PRIMARY KEY(id)
) ENGINE=InnoDB;

/* Use ids from backlog items */
INSERT INTO stories (id,storyPoints,name,
                   priority,state,description,backlog_id)
  SELECT item.id, item.originalEstimate/60,
         item.name, item.priority, item.state, item.description, item.backlog_id
  FROM backlogitem AS item
  INNER JOIN backlogs bl
  ON item.backlog_id = bl.id
  WHERE bl.backlogtype = 'Product' OR bl.backlogtype = 'Project';

/* Use automatic ids as there's only one refence to iteration goals
   to fix. */
INSERT INTO stories (name,priority,description,backlog_id,iterationGoal_id,state)
  SELECT item.name, item.priority,item.description,item.iteration_id,item.id,0
  FROM iterationgoal AS item;

/* Fix references from tasks to stories */
UPDATE tasks
SET story_id=(SELECT id FROM stories WHERE iterationGoal_id = tasks.story_id)
WHERE story_id IS NOT NULL;

/* If tasks have story as parent, remove the iteration association */
UPDATE tasks SET iteration_id = NULL WHERE story_id IS NOT NULL;

ALTER TABLE stories DROP COLUMN iterationGoal_id;

DROP TABLE iterationgoal;
DROP TABLE backlogitem;

/*** ASSOCIATIONS FROM USERS TO TASKS AND STORIES ***/
SELECT 'Pivot tables' AS status;

CREATE TABLE story_user (
  story_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  FOREIGN KEY (story_id) REFERENCES stories(id),
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB;

CREATE TABLE task_user (
  tasks_id INT(11) NOT NULL,
  responsibles_id INT(11) NOT NULL,
  FOREIGN KEY (tasks_id) REFERENCES tasks(id),
  FOREIGN KEY (responsibles_id) REFERENCES user(id)
) ENGINE=InnoDB;

INSERT INTO story_user
SELECT BacklogItem_id, user_id
FROM backlogitem_user bu
WHERE bu.BacklogItem_id IN (SELECT stories.id FROM stories);

INSERT INTO task_user
SELECT BacklogItem_id, user_id
FROM backlogitem_user bu
WHERE bu.BacklogItem_id IN (SELECT tasks.id FROM tasks);

DROP TABLE backlogitem_user;

/*** BUSINESS THEMES ***/
CREATE TABLE story_businesstheme (
  story_id INT(11) NOT NULL,
  businesstheme_id INT(11) NOT NULL,
  FOREIGN KEY (story_id) REFERENCES stories(id),
  FOREIGN KEY (businesstheme_id) REFERENCES businesstheme(id)
) ENGINE=InnoDB;

INSERT INTO story_businesstheme
SELECT backlogitem_id, businesstheme_id
FROM backlogitem_businesstheme
WHERE backlogitem_id IN (SELECT id FROM stories);

DROP TABLE backlogitem_businesstheme;

/*** History entries ***/

CREATE TABLE history_backlogs (
  id INTEGER NOT NULL AUTO_INCREMENT, 
  estimateSum BIGINT NOT NULL, 
  doneSum BIGINT NOT NULL, 
  timestamp DATETIME, 
  backlog_id INTEGER, 
  PRIMARY KEY (id),
  FOREIGN KEY (backlog_id) REFERENCES backlogs(id)
)ENGINE=InnoDB;
  
CREATE TABLE history_iterations (
  id INT(11) AUTO_INCREMENT,
  effortLeftSum BIGINT NOT NULL,
  originalEstimateSum BIGINT NOT NULL,
  deltaOriginalEstimate BIGINT NOT NULL,
  timestamp DATE NOT NULL,
  iteration_id INT(11) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (iteration_id) REFERENCES backlogs(id)
) ENGINE=InnoDB;

INSERT INTO history_iterations(effortLeftSum,originalEstimateSum,timestamp,iteration_id,
                               deltaOriginalEstimate)
SELECT effortLeft/60, originalEstimate/60, `date`, backlogs.id, deltaEffortLeft/60
FROM historyentry h
INNER JOIN backlogs
ON h.history_id = backlogs.history_fk
WHERE backlogs.backlogtype = 'Iteration';

/* New logic expects delta entries for today.
   Before they were written for yesterday */

SELECT 'Deleting all but the latest history entry for a day' AS status;

DROP TABLE IF EXISTS he_temp;
CREATE TEMPORARY TABLE he_temp SELECT * FROM history_iterations;

DELETE history_iterations
FROM history_iterations
INNER JOIN he_temp newer ON newer.id > history_iterations.id
  AND newer.iteration_id = history_iterations.iteration_id
  AND newer.timestamp = history_iterations.timestamp;

/* Add a constraint so that we can't get the db into a bad state any more */
ALTER TABLE history_iterations ADD CONSTRAINT UNIQUE (iteration_id, timestamp);

DROP TABLE he_temp;

SELECT 'Shift deltaOriginalEstimate forward' AS status;

CREATE TEMPORARY TABLE he_temp SELECT * FROM history_iterations;
UPDATE he_temp SET timestamp = (timestamp + INTERVAL 1 DAY);
ALTER TABLE he_temp DROP COLUMN id;

/* We don't want any entries to the future and they would be zero anyway */
DELETE FROM he_temp
WHERE timestamp > NOW();

DROP TABLE IF EXISTS he_temp_copy;

/* MySQL can't use a temporary table in two different subqueries */
CREATE TEMPORARY TABLE he_temp_copy SELECT * FROM he_temp;

UPDATE history_iterations SET deltaOriginalEstimate = 0;

UPDATE history_iterations
SET deltaOriginalEstimate=(
  SELECT deltaOriginalEstimate FROM he_temp
  WHERE he_temp.timestamp = history_iterations.timestamp
        AND  he_temp.iteration_id = history_iterations.iteration_id
)
WHERE (
  SELECT COUNT(*) FROM he_temp_copy t
  WHERE t.timestamp = history_iterations.timestamp
        AND  t.iteration_id = history_iterations.iteration_id
  ) > 0;

SELECT 'Delete updated from temporary table' AS status;

DELETE he_temp
FROM he_temp
INNER JOIN history_iterations
  ON he_temp.timestamp = history_iterations.timestamp
  AND he_temp.iteration_id = history_iterations.iteration_id;

SELECT 'Insert rest as new items' AS status;

/* Have to list columns manually because we need a new id */
INSERT INTO history_iterations (effortLeftSum, originalEstimateSum, deltaOriginalEstimate,
  timestamp, iteration_id)
SELECT effortLeftSum, originalEstimateSum, deltaOriginalEstimate,timestamp,
    iteration_id FROM he_temp;

/* Cleanup */ 

DROP TABLE historyentry;
ALTER TABLE backlogs DROP COLUMN history_fk;
DROP TABLE history;

/*** HOUR ENTRIES ***/

SELECT 'Hour entries' AS status;

UPDATE hourentry SET timeSpent=0 WHERE timeSpent IS NULL;
ALTER TABLE hourentry CHANGE timeSpent minutesSpent BIGINT(20) NOT NULL;
UPDATE hourentry SET minutesSpent=(minutesSpent/60);

ALTER TABLE hourentry ADD COLUMN story_id INT(11);
ALTER TABLE hourentry ADD COLUMN task_id INT(11);


UPDATE hourentry SET story_id=backlogItem_id, DTYPE='StoryHourEntry'
WHERE backlogItem_id IN (SELECT stories.id FROM stories);

UPDATE hourentry SET task_id=backlogItem_id, DTYPE='TaskHourEntry'
WHERE backlogItem_id IN (SELECT tasks.id FROM tasks);

ALTER TABLE hourentry DROP COLUMN backlogItem_id;

ALTER TABLE hourentry RENAME hourentries;

/*** MISC TABLES ***/

ALTER TABLE user RENAME users;
ALTER TABLE users DROP COLUMN description;
ALTER TABLE users CHANGE weekHours weekEffort BIGINT;
UPDATE users SET weekEffort=(weekEffort/60);

DROP TABLE worktype;

ALTER TABLE projecttype RENAME projecttypes;
ALTER TABLE projecttypes DROP COLUMN targetSpendingPercentage;

ALTER TABLE setting RENAME settings;
ALTER TABLE team RENAME teams;

/*** BRING BACK FOREIGN KEYS ***/

SELECT 'Foreign keys for assignment' AS status;

ALTER TABLE assignment ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);
ALTER TABLE assignment ADD FOREIGN KEY (user_id) REFERENCES users(id);

SELECT 'Foreign keys for backlogs' AS status;
ALTER TABLE backlogs ADD FOREIGN KEY (parent_id) REFERENCES backlogs(id);
ALTER TABLE backlogs ADD FOREIGN KEY (projectType_id) REFERENCES projecttypes(id);

SELECT 'Foreign keys for themes' AS status;
ALTER TABLE backlogthemebinding
  ADD FOREIGN KEY (businessTheme_id) REFERENCES businesstheme(id);
ALTER TABLE backlogthemebinding
  ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);

ALTER TABLE businesstheme ADD FOREIGN KEY (product_id) REFERENCES backlogs(id);

SELECT 'Foreign keys for hour entries' AS status;
ALTER TABLE hourentries ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE hourentries ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);
ALTER TABLE hourentries ADD FOREIGN KEY (story_id) REFERENCES stories(id);
ALTER TABLE hourentries ADD FOREIGN KEY (task_id) REFERENCES tasks(id);

SELECT 'Foreign keys for stories' AS status;
ALTER TABLE stories ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);

SELECT 'Foreign keys for tasks' AS status;
ALTER TABLE tasks ADD FOREIGN KEY (iteration_id) REFERENCES backlogs(id);
ALTER TABLE tasks ADD FOREIGN KEY (story_id) REFERENCES stories(id);

SELECT 'Foreign keys for team user pivot' AS status;
ALTER TABLE team_user ADD FOREIGN KEY (Team_id) REFERENCES teams(id);
ALTER TABLE team_user ADD FOREIGN KEY (User_id) REFERENCES users(id);
ALTER TABLE team_user ADD PRIMARY KEY (Team_id, User_id);

create table Holiday (id integer not null auto_increment, endDate date, startDate date, user_id integer, primary key (id)) ENGINE=InnoDB;
create table HolidayAnomaly (id integer not null auto_increment, date date, user_id integer, primary key (id)) ENGINE=InnoDB;
alter table Holiday add index FKA44DC038C1610AD2 (user_id), add constraint FKA44DC038C1610AD2 foreign key (user_id) references users (id);
alter table HolidayAnomaly add index FK267567ABC1610AD2 (user_id), add constraint FK267567ABC1610AD2 foreign key (user_id) references users (id);

/*** DROP TEMPORARY PROCEDURES ***/

SELECT 'Drop procedures' AS status;
DROP PROCEDURE ExecDyn;
DROP PROCEDURE DropFK;
DROP PROCEDURE DropAllForeignKeys;
DROP TABLE he_temp;
DROP TABLE he_temp_copy;
DROP TABLE items_with_todos;

/*** REVISIONING ***/
create table agilefant_revisions (id integer not null auto_increment, timestamp bigint not null, userName varchar(255), primary key (id)) ENGINE=InnoDB;
create table stories_AUD (id integer not null, REV integer not null, REVTYPE tinyint, description longtext, name varchar(255), rank integer, state integer, storyPoints integer, primary key (id, REV)) ENGINE=InnoDB;
create table story_user_AUD (REV integer not null, Story_id integer not null, User_id integer not null, REVTYPE tinyint, primary key (REV, Story_id, User_id)) ENGINE=InnoDB;
create table task_user_AUD (REV integer not null, tasks_id integer not null, responsibles_id integer not null, REVTYPE tinyint, primary key (REV, tasks_id, responsibles_id)) ENGINE=InnoDB;
create table tasks_AUD (id integer not null, REV integer not null, REVTYPE tinyint, description longtext, effortleft bigint, name varchar(255), originalestimate bigint, state integer, rank int default 0, primary key (id, REV)) ENGINE=InnoDB;
create table users_AUD (id integer not null, REV integer not null, REVTYPE tinyint, email varchar(255), enabled bit, fullName varchar(255), initials varchar(255), loginName varchar(255), weekEffort bigint, primary key (id, REV)) ENGINE=InnoDB;
alter table stories_AUD add index FK853E2CA420258526 (REV), add constraint FK853E2CA420258526 foreign key (REV) references agilefant_revisions (id);
alter table story_user_AUD add index FK9A1CB82620258526 (REV), add constraint FK9A1CB82620258526 foreign key (REV) references agilefant_revisions (id);
alter table task_user_AUD add index FKF0A49D9620258526 (REV), add constraint FKF0A49D9620258526 foreign key (REV) references agilefant_revisions (id);
alter table tasks_AUD add index FKBD8325F20258526 (REV), add constraint FKBD8325F20258526 foreign key (REV) references agilefant_revisions (id);
alter table users_AUD add index FK154C77D920258526 (REV), add constraint FK154C77D920258526 foreign key (REV) references agilefant_revisions (id);
alter table agilefant_revisions add column userId integer;

create table assignment_AUD (id integer not null, REV integer not null, REVTYPE tinyint, availability integer, delta_personal_load bigint, backlog_id integer, user_id integer, primary key (id, REV)) ENGINE=InnoDB;
create table backlogs_AUD (backlogtype varchar(31) not null, id integer not null, REV integer not null, REVTYPE tinyint, description longtext, name varchar(255), backlogSize integer, baselineLoad bigint, endDate datetime, rank integer, startDate datetime, status integer, primary key (id, REV)) ENGINE=InnoDB;
alter table assignment_AUD add index FK1B6D861E20258526 (REV), add constraint FK1B6D861E20258526 foreign key (REV) references agilefant_revisions (id);
alter table backlogs_AUD add index FK46C0B1E720258526 (REV), add constraint FK46C0B1E720258526 foreign key (REV) references agilefant_revisions (id);


-- UPDATE SCRIPT FOR RANKS

delimiter //

DROP PROCEDURE IF EXISTS UpdateTaskRanksForSingleStory //
CREATE PROCEDURE UpdateTaskRanksForSingleStory(IN sid INT)
BEGIN
  DECLARE story_task_loop_done BOOL DEFAULT FALSE;
  DECLARE counter INT DEFAULT 0;
  DECLARE task_id INT;
  DECLARE story_task_cur
    CURSOR FOR
    SELECT id FROM tasks WHERE story_id=sid
    ORDER BY priority DESC;


  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_task_loop_done = TRUE;

  OPEN story_task_cur;

  storyTaskLoop: LOOP
    FETCH story_task_cur INTO task_id;

    IF story_task_loop_done THEN
      CLOSE story_task_cur;
      LEAVE storyTaskLoop;
    END IF;

    UPDATE tasks SET rank = counter WHERE id=task_id;
    SET counter = counter + 1;

  END LOOP;
END //

DROP PROCEDURE IF EXISTS UpdateTaskRanksForSingleIteration //
CREATE PROCEDURE UpdateTaskRanksForSingleIteration(IN sid INT)
BEGIN
  DECLARE iter_task_loop_done BOOL DEFAULT FALSE;
  DECLARE counter INT DEFAULT 0;
  DECLARE task_id INT;
  DECLARE iter_task_cur
    CURSOR FOR
    SELECT id FROM tasks WHERE iteration_id=sid
    ORDER BY priority DESC;


  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET iter_task_loop_done = TRUE;

  OPEN iter_task_cur;

  iterTaskLoop: LOOP
    FETCH iter_task_cur INTO task_id;

    IF iter_task_loop_done THEN
      CLOSE iter_task_cur;
      LEAVE iterTaskLoop;
    END IF;

    UPDATE tasks SET rank = counter WHERE id=task_id;
    SET counter = counter + 1;

  END LOOP;
END //

DROP PROCEDURE IF EXISTS UpdateTaskRanksForStories //
CREATE PROCEDURE UpdateTaskRanksForStories()
BEGIN
  DECLARE story_loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;

  DECLARE story_cur CURSOR FOR SELECT id FROM stories;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_loop_done = TRUE;

  OPEN story_cur;

  storyLoop: LOOP
    FETCH story_cur INTO story_id;
    IF story_loop_done THEN
      CLOSE story_cur;
      LEAVE storyLoop;
    END IF;
    CALL UpdateTaskRanksForSingleStory(story_id);
  END LOOP;

END //

DROP PROCEDURE IF EXISTS UpdateTaskRanksForIterations //
CREATE PROCEDURE UpdateTaskRanksForIterations()
BEGIN
  DECLARE iter_loop_done BOOL DEFAULT FALSE;
  DECLARE iter_id INT;
  DECLARE iter_cur CURSOR FOR SELECT id FROM backlogs WHERE backlogtype = 'Iteration';

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET iter_loop_done = TRUE;

  OPEN iter_cur;

  iterLoop: LOOP
    FETCH iter_cur INTO iter_id;
    IF iter_loop_done THEN
      CLOSE iter_cur;
      LEAVE iterLoop;
    END IF;
    CALL UpdateTaskRanksForSingleIteration(iter_id);
  END LOOP;

END //

DROP PROCEDURE IF EXISTS UpdateTaskRanks //
CREATE PROCEDURE UpdateTaskRanks()
BEGIN
  CALL UpdateTaskRanksForStories();
  CALL UpdateTaskRanksForIterations();
END //

DROP PROCEDURE IF EXISTS UpdateStoryRanks //
CREATE PROCEDURE UpdateStoryRanks()
BEGIN
	DECLARE backlogId INT;
  DECLARE story_loop_done BOOL DEFAULT FALSE;
  DECLARE sl_cursor CURSOR FOR SELECT id FROM backlogs;
  
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_loop_done = TRUE;
    
  OPEN sl_cursor;
  
  storyLoop: LOOP
    FETCH sl_cursor INTO backlogId;
    IF story_loop_done THEN
      CLOSE sl_cursor;
      LEAVE storyLoop;
    END IF;
    CALL UpdateStoryRanksForBacklog(backlogId);
  END LOOP;
END //

DROP PROCEDURE IF EXISTS UpdateStoryRanksForBacklog //
CREATE PROCEDURE UpdateStoryRanksForBacklog(IN bid INT)
BEGIN
  DECLARE story_rank_loop_done BOOL DEFAULT FALSE;
  DECLARE counter INT DEFAULT 0;
  DECLARE story_id INT;
  DECLARE story_rank_cur
    CURSOR FOR
    SELECT id FROM stories WHERE backlog_id=bid
    ORDER BY priority ASC;


  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_rank_loop_done = TRUE;

  OPEN story_rank_cur;

  storyRankLoop: LOOP
    FETCH story_rank_cur INTO story_id;

    IF story_rank_loop_done THEN
      CLOSE story_rank_cur;
      LEAVE storyRankLoop;
    END IF;

    UPDATE stories SET rank = counter WHERE id=story_id;
    SET counter = counter + 1;

  END LOOP;
END //

delimiter ;

CALL UpdateTaskRanks();
CALL UpdateStoryRanks();

/* DROP the old columns */
ALTER TABLE tasks DROP COLUMN priority;
ALTER TABLE stories DROP COLUMN priority;

DROP PROCEDURE IF EXISTS UpdateTaskRanks;
DROP PROCEDURE IF EXISTS UpdateTaskRanksForStories;
DROP PROCEDURE IF EXISTS UpdateTaskRanksForIterations;
DROP PROCEDURE IF EXISTS UpdateTaskRanksForSingleStory;
DROP PROCEDURE IF EXISTS UpdateTaskRanksForSingleIteration;

DROP PROCEDURE IF EXISTS UpdateStoryRanksForBacklog;
DROP PROCEDURE IF EXISTS UpdateStoryRanks;



create table whatsnextentry (id integer not null auto_increment, rank int default 0 not null, task_id integer, user_id integer, primary key (id), unique (task_id, user_id)) ENGINE=InnoDB;
alter table whatsnextentry add index FK4B213410C1610AD2 (user_id), add constraint FK4B213410C1610AD2 foreign key (user_id) references users (id);
alter table whatsnextentry add index FK4B2134106E84F892 (task_id), add constraint FK4B2134106E84F892 foreign key (task_id) references tasks (id);

alter table stories add column parent_id integer;
alter table stories_AUD add column parent_id integer;
alter table stories add index FK8FB06053F5E897CD (parent_id), add constraint FK8FB06053F5E897CD foreign key (parent_id) references stories (id);

alter table users add column autoassignToTasks bit default 1;

/* after alpha 2 */

alter table history_backlogs add column rootSum bigint not null;


/* after alpha 3 */

create table storyrank (id integer not null auto_increment, backlog_id integer not null, rank integer not null default 0, story_id integer not null, primary key (id), unique (backlog_id, story_id)) ENGINE=InnoDB;
alter table storyrank add index FK6600C2A1F63400A2 (backlog_id), add constraint FK6600C2A1F63400A2 foreign key (backlog_id) references backlogs (id);
alter table storyrank add index FK6600C2A1E0E4BFA2 (story_id), add constraint FK6600C2A1E0E4BFA2 foreign key (story_id) references stories (id);

alter table stories add column treeRank int default 0;
alter table stories_AUD add column treeRank int default 0;

DELIMITER //

DROP PROCEDURE IF EXISTS StoryLinkedListRankForProject //
CREATE PROCEDURE StoryLinkedListRankForProject(IN bid INT)
BEGIN
  DECLARE story_rank_loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;
  DECLARE current_rank_num INT DEFAULT 0;
  DECLARE story_rank_cur
    CURSOR FOR
    SELECT stories.id  FROM stories stories 
        LEFT JOIN backlogs b1 ON b1.id = stories.backlog_id 
        LEFT JOIN backlogs b2 ON b2.id = b1.parent_id 
        WHERE NOT EXISTS (SELECT id FROM stories s2 WHERE s2.parent_id = stories.id) 
        AND ((b1.backlogtype = 'Project' AND b1.id = bid) 
        OR (b2.backlogtype = 'Project' AND b2.id = bid)) ORDER BY stories.rank;


  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_rank_loop_done = TRUE;

  OPEN story_rank_cur;

  storyRankLoop: LOOP
    FETCH story_rank_cur INTO story_id;

    IF story_rank_loop_done THEN
      CLOSE story_rank_cur;
      LEAVE storyRankLoop;
    END IF;


    INSERT INTO storyrank (`story_id`, `backlog_id`, `rank`) VALUES(story_id, bid, current_rank_num);
    SET current_rank_num = current_rank_num + 1;

  END LOOP;
END //

DROP PROCEDURE IF EXISTS UpdateProjectLeafStoryRanks //
CREATE PROCEDURE UpdateProjectLeafStoryRanks()
BEGIN
  DECLARE project_loop_done BOOL DEFAULT FALSE;
  DECLARE project_id INT;
  DECLARE cur_project
    CURSOR FOR
    SELECT id FROM backlogs WHERE backlogtype="Project" ORDER BY id;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET project_loop_done = TRUE;

  OPEN cur_project;

  projectLoop: LOOP
    FETCH cur_project INTO project_id;

    IF project_loop_done THEN
      CLOSE cur_project;
      LEAVE projectLoop;
    END IF;
    CALL StoryLinkedListRankForProject(project_id);
  END LOOP;
END //

DROP PROCEDURE IF EXISTS StoryRankForIteration //
CREATE PROCEDURE StoryRankForIteration(IN blid INT)
BEGIN
  DECLARE story_loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;
  DECLARE current_rank_num INT DEFAULT 0;
  DECLARE cur_story CURSOR FOR
    SELECT id FROM stories WHERE backlog_id=blid ORDER BY rank ASC;
  
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_loop_done = TRUE;
    
  OPEN cur_story;
  
  storyLoop: LOOP
    FETCH cur_story INTO story_id;
    
    IF story_loop_done THEN
      CLOSE cur_story;
      LEAVE storyLoop;
    END IF;
   
    INSERT INTO storyrank (`story_id`, `backlog_id`, `rank`) VALUES(story_id, blid, current_rank_num);
    SET current_rank_num = current_rank_num + 1;
  END LOOP;
END //


DROP PROCEDURE IF EXISTS UpdateIterationStoryRanks //
CREATE PROCEDURE UpdateIterationStoryRanks()
BEGIN
	DECLARE iteration_loop_done BOOL DEFAULT FALSE;
	DECLARE iteration_id INT;
	DECLARE cur_iteration CURSOR FOR
	  SELECT id FROM backlogs WHERE backlogtype="Iteration" ORDER BY id;
	
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
	  SET iteration_loop_done = TRUE;
	  
	OPEN cur_iteration;
	
	iterationLoop: LOOP
    FETCH cur_iteration INTO iteration_id;
    
    IF iteration_loop_done THEN
      CLOSE cur_iteration;
      LEAVE iterationLoop;
    END IF;
    CALL StoryRankForIteration(iteration_id);
	END LOOP;
END //

DROP PROCEDURE IF EXISTS UpdateStoryTreeRank //
CREATE PROCEDURE UpdateStoryTreeRank(IN parent_id INT)
BEGIN
  DECLARE story_loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;
  DECLARE current_rank INT DEFAULT 0;
  DECLARE tree_cur
    CURSOR FOR SELECT s.id FROM stories s WHERE s.parent_id = parent_id ORDER BY id;
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_loop_done = TRUE;

  OPEN tree_cur;

  story_tree_loop: LOOP
    FETCH tree_cur INTO story_id;

    IF story_loop_done THEN
      CLOSE tree_cur;
      LEAVE story_tree_loop;
    END IF;
    UPDATE stories set treeRank = current_rank WHERE id = story_id;
    SET current_rank = current_rank + 1;
  END LOOP;
END //

DROP PROCEDURE IF EXISTS SetTreeRanks //
CREATE PROCEDURE SetTreeRanks()
BEGIN
  DECLARE loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;
  /* all non root stories with children */
  DECLARE cur_stories CURSOR FOR
    SELECT story.id FROM stories as story 
        WHERE EXISTS (SELECT id FROM stories s2 WHERE s2.parent_id = story.id)
        ORDER BY story.parent_id ASC;          

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET loop_done = TRUE;
                      
  OPEN cur_stories;
                          
  storyLoop: LOOP
    FETCH cur_stories INTO story_id;
                                    
    IF loop_done THEN
      CLOSE cur_stories;
      LEAVE storyLoop;
    END IF;
    CALL UpdateStoryTreeRank(story_id);
  END LOOP;
END //


DELIMITER ;

CALL UpdateProjectLeafStoryRanks();
CALL UpdateIterationStoryRanks();
CALL SetTreeRanks();

DROP PROCEDURE IF EXISTS StoryLinkedListRankForProject;
DROP PROCEDURE IF EXISTS UpdateProjectLeafStoryRanks;
DROP PROCEDURE IF EXISTS StoryRankForIteration;
DROP PROCEDURE IF EXISTS UpdateIterationStoryRanks;
DROP PROCEDURE IF EXISTS UpdateStoryTreeRank;
DROP PROCEDURE IF EXISTS SetTreeRanks;

ALTER TABLE stories DROP COLUMN rank;

/** after alpha 4 **/

create table labels (id integer not null auto_increment, displayName varchar(255) not null, name varchar(255) not null, timestamp datetime, creator_id integer, story_id integer, backlog_id integer, primary key (id)) ENGINE=InnoDB;
alter table labels add index FKBDD05FFF1C5D0ED1 (creator_id), add constraint FKBDD05FFF1C5D0ED1 foreign key (creator_id) references users (id);
alter table labels add index FKBDD05FFFE0E4BFA2 (story_id), add constraint FKBDD05FFFE0E4BFA2 foreign key (story_id) references stories (id);

DELIMITER //

DROP PROCEDURE IF EXISTS ConvertStoryThemesToLabels //
CREATE PROCEDURE ConvertStoryThemesToLabels()
BEGIN
  DECLARE loop_done BOOL DEFAULT false;
  DECLARE story_id INT;
  DECLARE creator_id INT DEFAULT 1;
  DECLARE theme_name VARCHAR(255);
  DECLARE theme_description TEXT;
  DECLARE theme_cursor CURSOR FOR
    SELECT s.story_id,t.name,t.description FROM story_businesstheme s LEFT JOIN
    businesstheme t ON t.id = s.businesstheme_id ORDER BY t.id ASC;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET loop_done = TRUE;

  SELECT min(id) FROM users INTO creator_id;

  OPEN theme_cursor;

  theme_loop: LOOP
    FETCH theme_cursor INTO story_id, theme_name, theme_description;
    IF loop_done THEN
      CLOSE theme_cursor;
      LEAVE theme_loop;
    END IF;
    INSERT INTO labels (displayName, name, timestamp, creator_id, story_id)
      VALUES (theme_name, LOWER(theme_name), NOW(), 1, story_id);
  END LOOP;
END //

DROP PROCEDURE IF EXISTS ConvertBacklogThemesToLabels //
CREATE PROCEDURE ConvertBacklogThemesToLabels()
BEGIN
  DECLARE loop_done BOOL DEFAULT false;
  DECLARE backlog_id INT;
  DECLARE creator_id INT DEFAULT 1;
  DECLARE theme_name VARCHAR(255);
  DECLARE theme_description TEXT;
  DECLARE theme_cursor CURSOR FOR
    SELECT b.backlog_id,t.name,t.description FROM backlogthemebinding b LEFT JOIN
    businesstheme t ON t.id = b.businesstheme_id ORDER BY t.id ASC;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET loop_done = TRUE;

  SELECT min(id) FROM users INTO creator_id;

  OPEN theme_cursor;

  theme_loop: LOOP
    FETCH theme_cursor INTO backlog_id, theme_name, theme_description;
    IF loop_done THEN
      CLOSE theme_cursor;
      LEAVE theme_loop;
    END IF;
    INSERT INTO labels (displayName, name, timestamp, creator_id, backlog_id)
      VALUES (theme_name, LOWER(theme_name), NOW(), 1, backlog_id);
  END LOOP;
END //

DELIMITER ;

CALL ConvertStoryThemesToLabels();
CALL ConvertBacklogThemesToLabels();

DROP TABLE story_businesstheme;
DROP TABLE backlogthemebinding;
DROP TABLE businesstheme;

/** after alpha 5 **/

