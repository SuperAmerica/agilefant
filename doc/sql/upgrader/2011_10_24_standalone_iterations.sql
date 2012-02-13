alter table stories add column iteration_id integer DEFAULT NULL;

alter table stories add CONSTRAINT `FK_iteration_id` FOREIGN KEY (`iteration_id`) REFERENCES `backlogs` (`id`);

alter table stories_AUD add column iteration_id integer DEFAULT NULL;

alter table stories modify column backlog_id integer DEFAULT NULL;

INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '300', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="300";
