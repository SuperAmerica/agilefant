-- BacklogHourEntry muutokset kantaan 9.6.2008.
alter table hourentry add column backlog_id integer;
alter table hourentry add index FK3BF4210EF63400A2 (backlog_id), add constraint FK3BF4210EF63400A2 foreign key (backlog_id) references backlog (id);
alter table hourentry modify column backlogItem_id integer;

