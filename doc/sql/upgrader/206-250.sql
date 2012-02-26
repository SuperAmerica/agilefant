INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '250', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="250";
ALTER TABLE history_backlogs ADD COLUMN branchMax BIGINT(20) NOT NULL AFTER estimateSum;
