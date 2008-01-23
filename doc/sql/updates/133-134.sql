-- Add column initials to User table
ALTER TABLE User ADD COLUMN initials VARCHAR(255);

-- Copy 5 first letters of username to initials
UPDATE User SET initials = SUBSTRING(fullName, 1, 5);

-- add Assignment-class
create table Assignment (id integer not null auto_increment, user_id integer, backlog_id integer, primary key (id)) ENGINE=InnoDB;
alter table Assignment add index FKB3FD62EDF63400A2 (backlog_id), add constraint FKB3FD62EDF63400A2 foreign key (backlog_id) references Backlog (id);
alter table Assignment add index FKB3FD62EDC1610AD2 (user_id), add constraint FKB3FD62EDC1610AD2 foreign key (user_id) references User (id);
