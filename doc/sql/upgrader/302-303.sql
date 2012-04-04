INSERT INTO settings (`name`, `value`, `description`) VALUES ('AgilefantDatabaseVersion', '303', 'Agilefant database version') ON DUPLICATE KEY UPDATE `value`="303";
create table team_user_AUD (REV integer not null, Team_id integer not null, User_id integer not null, REVTYPE tinyint, primary key (REV, Team_id, User_id)) ENGINE=InnoDB;
alter table team_user_AUD add index FK7FE983BE20258526 (REV), add constraint FK7FE983BE20258526 foreign key (REV) references agilefant_revisions (id);
create table teams_AUD (id integer not null, REV integer not null, REVTYPE tinyint, description longtext, name varchar(255), primary key (id, REV)) ENGINE=InnoDB;
alter table teams_AUD add index FKF6966C8720258526 (REV), add constraint FKF6966C8720258526 foreign key (REV) references agilefant_revisions (id);
create table team_iteration_AUD (REV integer not null, Team_id integer not null, Iteration_id integer not null, REVTYPE tinyint, primary key (REV, Team_id, Iteration_id)) ENGINE=InnoDB;
alter table team_iteration_AUD add index FKFE5293CC20258526 (REV), add constraint FKFE5293CC20258526 foreign key (REV) references agilefant_revisions (id);
create table team_product_AUD (REV integer not null, Team_id integer not null, Product_id integer not null, REVTYPE tinyint, primary key (REV, Team_id, Product_id)) ENGINE=InnoDB;
alter table team_product_AUD add index FK4E84E85E20258526 (REV), add constraint FK4E84E85E20258526 foreign key (REV) references agilefant_revisions (id);
