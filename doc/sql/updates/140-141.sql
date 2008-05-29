-- create a setting table
create table setting (id integer not null auto_increment, name varchar(255) not null unique, value varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;

-- Create an hourentry table and target index
create table hourentry (DTYPE varchar(31) not null, id integer not null auto_increment, timeSpent integer, date datetime, description text, user_id integer, backlogItem_id integer not null, primary key (id)) ENGINE=InnoDB;
alter table hourentry add index FK3BF4210EE94683E2 (backlogItem_id), add constraint FK3BF4210EE94683E2 foreign key (backlogItem_id) references backlogitem (id);
alter table hourentry add index FK3BF4210EC1610AD2 (user_id), add constraint FK3BF4210EC1610AD2 foreign key (user_id) references user (id);
