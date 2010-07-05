-- Change the database version
INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '202', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="202";

-- Add the widgets
create table widgetcollections (id integer not null auto_increment, name varchar(255) not null, user_id integer, primary key (id)) ENGINE=InnoDB;
create table widgets (id integer not null auto_increment, listNumber integer, position integer, type varchar(255) not null, widgetCollection_id integer, objectId integer, primary key (id)) ENGINE=InnoDB;
alter table widgetcollections add index FK26C78D1C1610AD2 (user_id), add constraint FK26C78D1C1610AD2 foreign key (user_id) references users (id);
alter table widgets add index FK4FE3EEAF8BACA792 (widgetCollection_id), add constraint FK4FE3EEAF8BACA792 foreign key (widgetCollection_id) references widgetcollections (id);

-- Add user specific settings
alter table users add column autoassignToStories bit default 0;
alter table users add column markStoryBranchStarted integer default 1;
alter table users add column markStoryStarted integer default 1;