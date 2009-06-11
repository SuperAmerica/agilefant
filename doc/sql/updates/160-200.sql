/* If this is not set we can only create deterministic functions */
/* SET GLOBAL log_bin_trust_function_creators = 1; */

DROP PROCEDURE IF EXISTS DropFK;

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
			AND table_schema='agilefant';

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

CALL DropAllForeignKeys() //

delimiter ;

/*** ASSIGNMENT ***/

ALTER TABLE assignment
	CHANGE backlog_id project_id INT(11) DEFAULT NULL;
ALTER TABLE assignment
	CHANGE deltaOverhead delta_personal_load BIGINT(11) DEFAULT NULL;

/* convert from seconds to minutes */
UPDATE assignment SET delta_personal_load = (delta_personal_load / 60);

/*** BACKLOGS ***/

ALTER TABLE backlog RENAME backlogs;

ALTER TABLE backlogs DROP COLUMN assignee_id;
ALTER TABLE backlogs DROP COLUMN owner_id;
ALTER TABLE backlogs DROP COLUMN history_fk;

ALTER TABLE backlogs
	CHANGE project_id parent_id INT(11);

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
CREATE TEMPORARY TABLE items_with_todos (
	id INT(11)
);

/* find out items with todos */
INSERT INTO items_with_todos
SELECT DISTINCT i.id
	FROM backlogitem AS i
	INNER JOIN task AS t
	ON t.backlogItem_id = t.id;

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

CREATE TABLE tasks (
	id BIGINT AUTO_INCREMENT,
	createdDate DATETIME,
	effortLeft INT(11),
	originalestimate INT(11),
	name VARCHAR(255),
	priority INT(11),
	state INT(11),
	description text,
	iteration_id INT(11),
	creator_id INT(11),
	story_id BIGINT,
	PRIMARY KEY(id)
) ENGINE=InnoDB;

INSERT INTO tasks (id,createdDate,effortLeft,originalestimate,name,
                   priority,state,description,iteration_id,creator_id,story_id)
	SELECT item.id, item.createdDate, item.effortLeft/60, item.originalEstimate/60,
	       item.name, item.priority, item.state, item.description, item.backlog_id,
		   item.creator_id, item.iterationGoal_id
	FROM backlogitem AS item
	INNER JOIN backlogs bl
	ON item.backlog_id = bl.id
	WHERE bl.backlogtype = 'Iteration';

/* We have stuff coming here from both backlogitem
   and iterationgoal */
CREATE TABLE stories (
	id BIGINT AUTO_INCREMENT,
	createdDate DATETIME,
	storyPoints INT(11),
	name VARCHAR(255),
	priority INT(11),
	state INT(11),
	description text,
	creator_id INT(11),
	backlog_id INT(11),
	iterationGoal_id INT(11), /* temporary */
	PRIMARY KEY(id)
) ENGINE=InnoDB;

/* Use ids from backlog items */
INSERT INTO stories (id,createdDate,storyPoints,name,
                   priority,state,description,creator_id,backlog_id)
	SELECT item.id, item.createdDate, item.originalEstimate/60,
	       item.name, item.priority, item.state, item.description,
		   item.creator_id, item.backlog_id
	FROM backlogitem AS item
	INNER JOIN backlogs bl
	ON item.backlog_id = bl.id
	WHERE bl.backlogtype = 'Product' OR bl.backlogtype = 'Project';

/* Use automatic ids as there's only one refence to iteration goals
   to fix. */
INSERT INTO stories (name,priority,description,backlog_id,iterationGoal_id)
	SELECT item.name, item.priority,item.description,item.iteration_id,item.id
	FROM iterationgoal AS item;

/* Fix references from tasks to stories */
UPDATE tasks
SET story_id=(SELECT id FROM stories WHERE iterationGoal_id = tasks.story_id)
WHERE story_id IS NOT NULL;

ALTER TABLE stories DROP COLUMN iterationGoal_id;

DROP TABLE iterationgoal;
DROP TABLE backlogitem;

/*** ASSOCIATIONS FROM USERS TO TASKS AND STORIES ***/
CREATE TABLE story_users (
	story_id INT(11) NOT NULL REFERENCES stories(id),
	user_id INT(11) NOT NULL REFERENCES user(id),
	PRIMARY KEY(story_id,user_id)
) ENGINE=InnoDB;

CREATE TABLE tasks_users (
	task_id INT(11) NOT NULL REFERENCES tasks(id),
	user_id INT(11) NOT NULL REFERENCES user(id),
	PRIMARY KEY(task_id,user_id)
) ENGINE=InnoDB;

INSERT INTO story_users
SELECT BacklogItem_id, user_id
FROM backlogitem_user bu
WHERE bu.BacklogItem_id IN (SELECT stories.id FROM stories);

INSERT INTO tasks_users
SELECT BacklogItem_id, user_id
FROM backlogitem_user bu
WHERE bu.BacklogItem_id IN (SELECT tasks.id FROM tasks);

DROP TABLE backlogitem_user;

/*** BUSINESS THEMES ***/
CREATE TABLE story_businesstheme (
	story_id INT(11) NOT NULL REFERENCES story(id),
	businesstheme_id INT(11) NOT NULL REFERENCES businesstheme(id),
	PRIMARY KEY(story_id,businesstheme_id)
) ENGINE=InnoDB;

INSERT INTO story_businesstheme
SELECT backlogitem_id, businesstheme_id
FROM backlogitem_businesstheme
WHERE backlogitem_id IN (SELECT id FROM stories);

DROP TABLE backlogitem_businesstheme;

/*** History entries ***/

DROP TABLE history;

CREATE TABLE history_iterations (
	id BIGINT AUTO_INCREMENT,
	effortLeftSum INT(11) NOT NULL,
	originalEstimateSum INT(11) NOT NULL,
	deltaEffortLeft INT(11) NOT NULL DEFAULT 0,
	deltaOriginalEstimate INT(11) NOT NULL DEFAULT 0,
	timestamp DATETIME,
	iteration_id INT(11),
	PRIMARY KEY(id)
) ENGINE=InnoDB;

INSERT INTO history_iterations(effortLeftSum,originalEstimateSum,timestamp,iteration_id,
                               deltaEffortLeft)
SELECT effortLeft, originalEstimate, date, history_id, deltaEffortLeft
FROM historyentry h
INNER JOIN backlogs
ON h.history_id = backlogs.id
WHERE backlogs.backlogtype = 'Iteration';

DROP TABLE historyentry;

/*** MISC TABLES ***/
ALTER TABLE hourentry CHANGE timeSpent minutesSpent BIGINT(20);
UPDATE hourentry SET minutesSpent=(minutesSpent/60);

ALTER TABLE hourentry ADD COLUMN story_id BIGINT;
ALTER TABLE hourentry ADD COLUMN task_id BIGINT;


UPDATE hourentry SET story_id=backlogItem_id
WHERE backlogItem_id IN (SELECT stories.id FROM stories);

UPDATE hourentry SET task_id=backlogItem_id
WHERE backlogItem_id IN (SELECT tasks.id FROM tasks);

ALTER TABLE hourentry DROP COLUMN backlogItem_id;

ALTER TABLE user RENAME users;

DROP TABLE worktype;

/*** BRING BACK FOREIGN KEYS ***/

SELECT 'Foreign keys for assignment' AS status;

ALTER TABLE assignment ADD FOREIGN KEY (project_id) REFERENCES backlogs(id);
ALTER TABLE assignment ADD FOREIGN KEY (user_id) REFERENCES users(id);

SELECT 'Foreign keys for backlogs' AS status;
ALTER TABLE backlogs ADD FOREIGN KEY (parent_id) REFERENCES backlogs(id);
ALTER TABLE backlogs ADD FOREIGN KEY (projectType_id) REFERENCES projecttype(id);

SELECT 'Foreign keys for themes' AS status;
ALTER TABLE backlogthemebinding
	ADD FOREIGN KEY (businessTheme_id) REFERENCES businesstheme(id);
ALTER TABLE backlogthemebinding
	ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);

ALTER TABLE businesstheme ADD FOREIGN KEY (product_id) REFERENCES backlogs(id);

SELECT 'Foreign keys for hour entries' AS status;
ALTER TABLE hourentry ADD FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE hourentry ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);
ALTER TABLE hourentry ADD FOREIGN KEY (story_id) REFERENCES stories(id);
ALTER TABLE hourentry ADD FOREIGN KEY (task_id) REFERENCES tasks(id);

SELECT 'Foreign keys for stories' AS status;
ALTER TABLE stories ADD FOREIGN KEY (creator_id) REFERENCES users(id);
ALTER TABLE stories ADD FOREIGN KEY (backlog_id) REFERENCES backlogs(id);

SELECT 'Foreign keys for tasks' AS status;
ALTER TABLE tasks ADD FOREIGN KEY (iteration_id) REFERENCES backlogs(id);
ALTER TABLE tasks ADD FOREIGN KEY (creator_id) REFERENCES users(id);
ALTER TABLE tasks ADD FOREIGN KEY (story_id) REFERENCES stories(id);

SELECT 'Foreign keys for team user pivot' AS status;
ALTER TABLE team_user ADD FOREIGN KEY (Team_id) REFERENCES team(id);
ALTER TABLE team_user ADD FOREIGN KEY (User_id) REFERENCES users(id);
ALTER TABLE team_user ADD PRIMARY KEY (Team_id, User_id);
