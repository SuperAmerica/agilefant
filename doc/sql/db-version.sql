INSERT INTO settings (`name`,`value`, `description`) VALUES ("AgilefantDatabaseVersion", "300", "Agilefant database version")
  ON DUPLICATE KEY UPDATE `value`="300";
