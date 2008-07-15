-- Clean up old themes
drop table if exists backlogitem_businesstheme;
drop table if exists businesstheme;

-- Drop unused table
drop table if exists taskevent;

-- Themes moved under products, name not unique anymore
create table businesstheme (id integer not null auto_increment, name varchar(255) not null, description text, product_id integer, active boolean default 1, primary key (id)) ENGINE=InnoDB;
alter table businesstheme add index FK26B08E09A7FE2362 (product_id), add constraint FK26B08E09A7FE2362 foreign key (product_id) references backlog (id);
create table backlogitem_businesstheme (backlogitem_id integer not null, businesstheme_id integer not null, primary key (backlogitem_id, businesstheme_id)) ENGINE=InnoDB;
alter table backlogitem_businesstheme add index FKE72E399AE94683E2 (backlogitem_id), add constraint FKE72E399AE94683E2 foreign key (backlogitem_id) references backlogitem (id);
alter table backlogitem_businesstheme add index FKE72E399AE01C3F02 (businesstheme_id), add constraint FKE72E399AE01C3F02 foreign key (businesstheme_id) references businesstheme (id);

-- update database to use utf8 as default encoding
ALTER TABLE `backlog` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `backlogitem` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `backlogitem_businesstheme` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `backlogitem_user` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `businesstheme` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `history` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `historyentry` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `hourentry` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `iterationgoal` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `projecttype` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `setting` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `task` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `team` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `team_user` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `user` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `worktype` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `assignment` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER DATABASE CHARACTER SET utf8 COLLATE utf8_general_ci;