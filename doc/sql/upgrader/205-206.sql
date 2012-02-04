INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '206', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="206";
ALTER TABLE stories ADD COLUMN storyValue INT AFTER storyPoints;
ALTER TABLE stories_AUD ADD COLUMN storyValue INT AFTER storyPoints;
