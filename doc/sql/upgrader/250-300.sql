INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '300', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="300";
ALTER TABLE users ADD COLUMN admin bit DEFAULT 1 AFTER id;
create table team_product (Team_id integer not null, Product_id integer not null) ENGINE=InnoDB;
alter table team_product add index FK65CE090D745BA992 (Team_id), add constraint FK65CE090D745BA992 foreign key (Team_id) references teams (id);
alter table team_product add index FK65CE090DA7FE2362 (Product_id), add constraint FK65CE090DA7FE2362 foreign key (Product_id) references backlogs (id);
ALTER TABLE backlogs ADD COLUMN readonlyToken varchar(255) unique;
INSERT INTO users (admin, loginName, enabled, recentItemsNumberOfWeeks) VALUES (0, "readonly", 1, 0);

-- Access rights for stand alone iterations
create table team_iteration (Team_id integer not null, Iteration_id integer not null) ENGINE=InnoDB;
alter table team_iteration add index FKF2269B7B4157D2A2 (Iteration_id), add constraint FKF2269B7B4157D2A2 foreign key (Iteration_id) references backlogs (id);
alter table team_iteration add index FKF2269B7B745BA992 (Team_id), add constraint FKF2269B7B745BA992 foreign key (Team_id) references teams (id);