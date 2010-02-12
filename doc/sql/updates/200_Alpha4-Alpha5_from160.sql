/**
 * NOTE: This script should be used if data from Agilefant 1.6 version has been converted to 2.0 Alpha 4 version.
 * If you have clean 2.0 installation use 200_Alpha4-Alpha5.sql.
 **/
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

