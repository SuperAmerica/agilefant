INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '304', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="304";
DELETE history_backlogs FROM history_backlogs INNER JOIN backlogs ON history_backlogs.backlog_id = backlogs.id AND backlogs.backlogtype <> 'Project';
