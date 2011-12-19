INSERT INTO settings (`name`,`value`, `description`) VALUES ("AgilefantDatabaseVersion", "205", "Agilefant database version")
  ON DUPLICATE KEY UPDATE `value`="205";
