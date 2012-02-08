INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '206', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="206";
ALTER TABLE history_backlogs ADD COLUMN branchMax BIGINT(20) NOT NULL AFTER estimateSum;
ALTER TABLE stories ADD COLUMN storyValue INT AFTER storyPoints;
ALTER TABLE stories_AUD ADD COLUMN storyValue INT AFTER storyPoints;
