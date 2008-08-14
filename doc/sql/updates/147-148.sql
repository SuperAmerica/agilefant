alter table backlog add column backlogSize integer;

create table backlogthemebinding (id integer not null auto_increment, relativeBinding boolean not null default false, percentage float, fixedSize bigint, businessTheme_id integer, backlog_id integer, primary key (id)) ENGINE=InnoDB;

alter table backlogthemebinding add index FKEF836219E01C3F02 (businessTheme_id), add constraint FKEF836219E01C3F02 foreign key (businessTheme_id) references businesstheme (id);
alter table backlogthemebinding add index FKEF836219E537EC82 (backlog_id), add constraint FKEF836219E537EC82 foreign key (backlog_id) references backlog (id);
alter table backlogthemebinding add index FKEF836219F8762ABE (backlog_id), add constraint FKEF836219F8762ABE foreign key (backlog_id) references backlog (id);
alter table backlogthemebinding add index FKEF836219F63400A2 (backlog_id), add constraint FKEF836219F63400A2 foreign key (backlog_id) references backlog (id);