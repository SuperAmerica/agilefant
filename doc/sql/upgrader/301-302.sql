INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '302', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="302";
DELETE storyrank FROM storyrank INNER JOIN backlogs ON storyrank.backlog_id = backlogs.id AND backlogs.backlogtype = 'Product';
