-- Add column initials to User table
ALTER TABLE User ADD COLUMN initials VARCHAR(255);

-- Copy 5 first letters of username to initials
UPDATE User SET initials = TRIM(SUBSTRING(fullName, 1, 5));

-- Add the responsibles-relation to database
DROP TABLE IF EXISTS BacklogItem_User;
CREATE TABLE BacklogItem_User (BacklogItem_id INTEGER NOT NULL, User_id INTEGER NOT NULL);
ALTER TABLE BacklogItem_User ADD INDEX FK4CB3A13AE94683E2 (BacklogItem_id), ADD CONSTRAINT FK4CB3A13AE94683E2 FOREIGN KEY (BacklogItem_id) REFERENCES BacklogItem (id);
ALTER TABLE BacklogItem_User ADD INDEX FK4CB3A13AC1610AD2 (User_id), ADD CONSTRAINT FK4CB3A13AC1610AD2 FOREIGN KEY (User_id) REFERENCES User (id);

-- Copy the information on assignees to responsibles-relation
INSERT INTO BacklogItem_User (BacklogItem_id, User_id) SELECT id, assignee_id FROM BacklogItem WHERE assignee_id IS NOT NULL;

-- add Assignment-class
create table Assignment (id integer not null auto_increment, user_id integer, backlog_id integer, primary key (id)) ENGINE=InnoDB;
alter table Assignment add index FKB3FD62EDF63400A2 (backlog_id), add constraint FKB3FD62EDF63400A2 foreign key (backlog_id) references Backlog (id);
alter table Assignment add index FKB3FD62EDC1610AD2 (user_id), add constraint FKB3FD62EDC1610AD2 foreign key (user_id) references User (id);

-- Add Teams
create table Team (id integer not null auto_increment, name varchar(255), description text, primary key (id)) ENGINE=InnoDB;
create table Team_User (Team_id integer not null, User_id integer not null) ENGINE=InnoDB;
alter table Team_User add index FK6CEAE86DC1610AD2 (User_id), add constraint FK6CEAE86DC1610AD2 foreign key (User_id) references User (id);
alter table Team_User add index FK6CEAE86D745BA992 (Team_id), add constraint FK6CEAE86D745BA992 foreign key (Team_id) references Team (id);