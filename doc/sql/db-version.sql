INSERT INTO settings (`name`,`value`, `description`) VALUES ("AgilefantDatabaseVersion", "200b2", "Agilefant database version")
  ON DUPLICATE KEY UPDATE `value`="200b2";