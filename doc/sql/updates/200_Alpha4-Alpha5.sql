create table labels (id integer not null auto_increment, displayName varchar(255) not null, name varchar(255) not null, timestamp datetime, creator_id integer, story_id integer, backlog_id integer, primary key (id)) ENGINE=InnoDB;
alter table labels add index FKBDD05FFF1C5D0ED1 (creator_id), add constraint FKBDD05FFF1C5D0ED1 foreign key (creator_id) references users (id);
alter table labels add index FKBDD05FFFE0E4BFA2 (story_id), add constraint FKBDD05FFFE0E4BFA2 foreign key (story_id) references stories (id);


