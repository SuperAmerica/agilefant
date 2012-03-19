INSERT INTO settings (`name`,`value`, `description`) VALUES ("AgilefantDatabaseVersion", "301", "Agilefant database version")
  ON DUPLICATE KEY UPDATE `value`="301";
