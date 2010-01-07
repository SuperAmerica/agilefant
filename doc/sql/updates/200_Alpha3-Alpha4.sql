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