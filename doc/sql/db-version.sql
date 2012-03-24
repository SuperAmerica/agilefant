INSERT INTO settings (`name`,`value`, `description`) VALUES ("AgilefantDatabaseVersion", "250", "Agilefant database version")
  ON DUPLICATE KEY UPDATE `value`="250";
