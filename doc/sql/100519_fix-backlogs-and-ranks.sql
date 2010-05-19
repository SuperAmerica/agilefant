
DROP PROCEDURE IF EXISTS UpdateBacklogsAndRanks;

DELIMITER //

CREATE PROCEDURE UpdateBacklogsAndRanks()
BEGIN
	DECLARE backlog_loop_done BOOL DEFAULT FALSE;
	DECLARE story_id INT;
	DECLARE target_backlog_id INT;
  DECLARE cur_update
    CURSOR FOR
    SELECT s.backlog_id, st.id FROM storyrank s JOIN stories st ON st.id=s.story_id WHERE s.backlog_id IN (SELECT id FROM backlogs bl WHERE backlogtype = 'Iteration') AND st.backlog_id != s.backlog_id;
	
  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET backlog_loop_done = TRUE;
    
  OPEN cur_update;
  
  backlogLoop: LOOP
    FETCH cur_update INTO target_backlog_id, story_id;
    
    IF backlog_loop_done THEN
      CLOSE cur_update;
      LEAVE backlogLoop;
    END IF;
    
    UPDATE stories SET backlog_id = target_backlog_id WHERE id=story_id;
  END LOOP;
END //

DELIMITER ;

CALL UpdateBacklogsAndRanks();
DROP PROCEDURE IF EXISTS UpdateBacklogsAndRanks;
