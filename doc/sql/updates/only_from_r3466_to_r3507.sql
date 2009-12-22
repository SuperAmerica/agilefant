/*
* This script changes the story ranking cahnges done in revision 3466 to new format.
* TO BE USED ONLY FOR UPDATING FROM REVISIONS 3466-3506 TO REVISION 3507
*/
DELIMITER //

DROP PROCEDURE IF EXISTS Update3500RanksInBacklog //
CREATE PROCEDURE Update3500RanksInBacklog(IN bid INT)
BEGIN
  DECLARE loop_done BOOL DEFAULT FALSE;
  DECLARE prev_id INT DEFAULT NULL;
  DECLARE rank_id INT;
  DECLARE story_id INT;
  DECLARE current_rank INT DEFAULT 1;

 SELECT id FROM storyrank WHERE backlog_id = bid AND previous_id IS NULL INTO prev_id;
 UPDATE storyrank SET rank = 0 WHERE backlog_id = bid AND previous_id IS NULL;
  storyRankLoop: LOOP
    SET rank_id = null;
    SELECT id FROM storyrank WHERE backlog_id = bid AND previous_id = prev_id LIMIT 1 INTO rank_id;
    IF rank_id is null THEN
      LEAVE storyRankLoop;
    END IF;
    UPDATE storyrank SET rank = current_rank WHERE id = rank_id;
    SET prev_id = rank_id;
    SET current_rank = current_rank + 1;

  END LOOP;
END //

DROP PROCEDURE IF EXISTS Update3500StoryRanks //
CREATE PROCEDURE Update3500StoryRanks()
BEGIN
  DECLARE loop_done BOOL DEFAULT FALSE;
  DECLARE bid INT;
  DECLARE cur_backlog
    CURSOR FOR
    SELECT backlog_id FROM storyrank GROUP BY backlog_id ORDER BY backlog_id;

  DECLARE CONTINUE HANDLER FOR SQLSTATE '02000'
    SET loop_done = TRUE;

  OPEN cur_backlog;

  bloop: LOOP
    FETCH cur_backlog INTO bid;
    IF loop_done THEN
      CLOSE cur_backlog;
      LEAVE bloop;
    END IF;
    CALL Update3500RanksInBacklog(bid);
  END LOOP;
END //

DELIMITER ;

alter table storyrank add column rank integer not null default 0;

CALL Update3500StoryRanks();

UPDATE storyrank SET rank = 0 WHERE rank IS NULL;
DROP PROCEDURE IF EXISTS Update3500RanksInBacklog;
DROP PROCEDURE IF EXISTS Update3500StoryRanks;

ALTER TABLE storyrank
 DROP FOREIGN KEY FK6600C2A1CD3EA02C;
ALTER TABLE storyrank
 DROP FOREIGN KEY FK6600C2A1774AFEB0;

ALTER TABLE storyrank DROP COLUMN previous_id;
ALTER TABLE storyRANK DROP COLUMN next_id;