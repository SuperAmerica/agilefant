INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '300', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="300";
ALTER TABLE users ADD COLUMN admin bit DEFAULT 1 AFTER id;
create table team_product (Team_id integer not null, Product_id integer not null) ENGINE=InnoDB;
alter table team_product add index FK65CE090D745BA992 (Team_id), add constraint FK65CE090D745BA992 foreign key (Team_id) references teams (id);
alter table team_product add index FK65CE090DA7FE2362 (Product_id), add constraint FK65CE090DA7FE2362 foreign key (Product_id) references backlogs (id);