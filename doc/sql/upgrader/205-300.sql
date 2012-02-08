alter table stories add column iteration_id integer DEFAULT NULL;

alter table stories add CONSTRAINT `FK_iteration_id` FOREIGN KEY (`iteration_id`) REFERENCES `backlogs` (`id`);

alter table stories_AUD add column iteration_id integer DEFAULT NULL;

alter table stories modify column backlog_id integer DEFAULT NULL;

INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '300', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="300";

DELIMITER //

DROP PROCEDURE IF EXISTS UpdateStoryIterationRefs //

CREATE PROCEDURE UpdateStoryIterationRefs()
BEGIN
  DECLARE stories_loop_done BOOL DEFAULT FALSE;
  DECLARE story_id INT;
  DECLARE backlog_id INT;
  DECLARE project_id INT;
  DECLARE cur_story
    CURSOR FOR
    SELECT stories.id, stories.backlog_id FROM stories, backlogs WHERE backlogs.id = stories.backlog_id AND backlogs.backlogtype = 'Iteration';

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET stories_loop_done = TRUE;

  OPEN cur_story;

  storyLoop: LOOP
    FETCH cur_story INTO story_id, backlog_id;

    SELECT parent_id INTO project_id FROM backlogs WHERE id = backlog_id;

    IF stories_loop_done THEN
      CLOSE cur_story;
      LEAVE storyLoop;
    END IF;

    UPDATE stories SET iteration_id = backlog_id WHERE id = story_id;
    UPDATE stories SET backlog_id = project_id;
  END LOOP;
END //

DELIMITER ;

CALL UpdateStoryIterationRefs();

DROP PROCEDURE IF EXISTS UpdateStoryIterationRefs;
