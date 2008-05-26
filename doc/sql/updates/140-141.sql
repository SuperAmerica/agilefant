-- create a setting table
create table setting (id integer not null auto_increment, name varchar(255) not null unique, value varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
