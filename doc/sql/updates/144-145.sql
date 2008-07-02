-- add businessThemes
create table businesstheme (id integer not null auto_increment, name varchar(255) not null unique, description text, primary key (id)) ENGINE=InnoDB;
create table backlogitem_businesstheme (backlogitem_id integer not null, businesstheme_id integer not null, primary key (backlogitem_id, businesstheme_id)) ENGINE=InnoDB;