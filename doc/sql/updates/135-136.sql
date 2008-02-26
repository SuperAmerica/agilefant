alter table Assignment add column deltaOverhead integer;
alter table Backlog add column defaultOverhead integer;
alter table BacklogItem_User add index FK4CB3A13AE94683E2 (BacklogItem_id), add constraint FK4CB3A13AE94683E2 foreign key (BacklogItem_id) references BacklogItem (id);
alter table BacklogItem_User add index FK4CB3A13AC1610AD2 (User_id), add constraint FK4CB3A13AC1610AD2 foreign key (User_id) references User (id);