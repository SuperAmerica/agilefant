
-- Remove duplicate entries
CREATE TABLE temp_table AS SELECT * FROM task_user GROUP BY tasks_id, responsibles_id;
DROP TABLE task_user;
RENAME TABLE temp_table TO task_user;

ALTER TABLE task_user ADD INDEX FKAC91A45B1C109E9 (tasks_id), ADD CONSTRAINT FKAC91A45B1C109E9 foreign key (tasks_id) REFERENCES tasks (id);
ALTER TABLE task_user ADD INDEX FKAC91A4527F4B120 (responsibles_id), ADD CONSTRAINT FKAC91A4527F4B120 foreign key (responsibles_id) REFERENCES users (id);


-- Update hour entries
UPDATE hourentries SET story_id=null WHERE story_id is not null AND DTYPE = 'TaskHourEntry'; 

alter table backlogs_AUD add column parent_id integer;
alter table stories_AUD add column backlog_id integer;
alter table tasks_AUD add column iteration_id integer;
alter table tasks_AUD add column story_id integer;

INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '200b2', 'Agilefant database version');


-- Fix tree ranks
﻿
DROP PROCEDURE IF EXISTS UpdateTreeRanksForChildren;
DROP PROCEDURE IF EXISTS UpdateStoryRanksByParents;

DELIMITER //

CREATE PROCEDURE UpdateTreeRanksForChildren(IN par_id INT)
BEGIN
  DECLARE child_loop_done BOOL DEFAULT FALSE;
  DECLARE child_id INT;
  DECLARE current_rank_no INT DEFAULT 0;
  DECLARE cur_child
    CURSOR FOR
    SELECT DISTINCT id FROM stories WHERE parent_id=par_id ORDER BY treeRank ASC;
    
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET child_loop_done = TRUE;
    
  OPEN cur_child;
  
  childLoop: LOOP
    FETCH cur_child INTO child_id;
    
    IF child_loop_done THEN
      CLOSE cur_child;
      LEAVE childLoop;
    END IF;

    UPDATE stories SET treeRank=current_rank_no WHERE id=child_id;
    SET current_rank_no = current_rank_no + 1;
  END LOOP;
END //

CREATE PROCEDURE UpdateStoryRanksByParents()
BEGIN
  DECLARE story_loop_done BOOL DEFAULT FALSE;
  DECLARE par_id INT;
  DECLARE cur_story
    CURSOR FOR
    SELECT DISTINCT parent_id FROM stories WHERE parent_id IS NOT NULL;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET story_loop_done = TRUE;
    
  OPEN cur_story;
  
  storyLoop: LOOP
    FETCH cur_story INTO par_id;
    
    IF story_loop_done THEN
      CLOSE cur_story;
      LEAVE storyLoop;
    END IF;
    
    CALL UpdateTreeRanksForChildren(par_id);
  END LOOP;
END //

DELIMITER ;

CALL UpdateStoryRanksByParents();

DROP PROCEDURE IF EXISTS UpdateStoryRanksByParents;
DROP PROCEDURE IF EXISTS UpdateTreeRanksForChildren;