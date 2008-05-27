-- create a setting table
create table setting (id integer not null auto_increment, name varchar(255) not null unique, value varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;

-- Create an hourentry table and target index
create table hourentry (id integer not null auto_increment, targetId integer not null, targetType varchar(255) not null, timeSpent integer, date datetime, description text, user_id integer, primary key (id)) ENGINE=InnoDB;
create index idx_hourentry_target on hourentry (targetId, targetType);
