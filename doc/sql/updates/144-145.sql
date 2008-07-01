-- add businessThemes
create table businesstheme (id integer not null auto_increment, name varchar(255) not null unique, description text, primary key (id)) ENGINE=InnoDB;