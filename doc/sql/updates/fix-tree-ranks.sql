
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