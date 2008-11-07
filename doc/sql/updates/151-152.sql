alter table backlogitem add column createdDate datetime;
alter table backlogitem add column creator_id integer;
alter table backlogitem add index FK655CD5901C5D0ED1 (creator_id), add constraint FK655CD5901C5D0ED1 foreign key (creator_id) references user (id);