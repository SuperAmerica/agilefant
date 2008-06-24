-- Add the possibility to scope
alter table historyentry add column deltaEffortLeft integer default 0;
update historyentry set deltaEffortLeft = 0 where deltaEffortLeft = null;

-- Load-mittarin muutokset
alter table user add column weekHours integer default 144000;

-- Other regression fixes
alter table assignment add index FK3D2B86CDF63400A2 (backlog_id), add constraint FK3D2B86CDF63400A2 foreign key (backlog_id) references backlog (id);
alter table assignment add index FK3D2B86CDC1610AD2 (user_id), add constraint FK3D2B86CDC1610AD2 foreign key (user_id) references user (id);
alter table backlog add index FKEB4E20FDCA187B22 (project_id), add constraint FKEB4E20FDCA187B22 foreign key (project_id) references backlog (id);
alter table backlog add index FKEB4E20FDC91A641F (history_fk), add constraint FKEB4E20FDC91A641F foreign key (history_fk) references history (id);
alter table backlog add index FKEB4E20FD31FA7A4E (assignee_id), add constraint FKEB4E20FD31FA7A4E foreign key (assignee_id) references user (id);
alter table backlog add index FKEB4E20FDA7FE2362 (product_id), add constraint FKEB4E20FDA7FE2362 foreign key (product_id) references backlog (id);
alter table backlog add index FKEB4E20FD3872F902 (projectType_id), add constraint FKEB4E20FD3872F902 foreign key (projectType_id) references projecttype (id);
alter table backlog add index FKEB4E20FD2D47BAEA (owner_id), add constraint FKEB4E20FD2D47BAEA foreign key (owner_id) references user (id);
alter table backlogitem add index FK655CD5907A2D5E2 (iterationGoal_id), add constraint FK655CD5907A2D5E2 foreign key (iterationGoal_id) references iterationgoal (id);
alter table backlogitem add index FK655CD59031FA7A4E (assignee_id), add constraint FK655CD59031FA7A4E foreign key (assignee_id) references user (id);
alter table backlogitem add index FK655CD590F63400A2 (backlog_id), add constraint FK655CD590F63400A2 foreign key (backlog_id) references backlog (id);
alter table backlogitem_user add index FK78BC91AE94683E2 (BacklogItem_id), add constraint FK78BC91AE94683E2 foreign key (BacklogItem_id) references backlogitem (id);
alter table backlogitem_user add index FK78BC91AC1610AD2 (User_id), add constraint FK78BC91AC1610AD2 foreign key (User_id) references user (id);
alter table historyentry add index FK8B5EE05EC91A6475 (history_id), add constraint FK8B5EE05EC91A6475 foreign key (history_id) references history (id);
alter table historyentry add index FK8B5EE05EFD7DC542 (history_id), add constraint FK8B5EE05EFD7DC542 foreign key (history_id) references history (id);
alter table iterationgoal add index FK8D38B7704157D2A2 (iteration_id), add constraint FK8D38B7704157D2A2 foreign key (iteration_id) references backlog (id);
alter table task add index FK363585E94683E2 (backlogItem_id), add constraint FK363585E94683E2 foreign key (backlogItem_id) references backlogitem (id);
alter table task add index FK3635851C5D0ED1 (creator_id), add constraint FK3635851C5D0ED1 foreign key (creator_id) references user (id);
alter table team_user add index FKF587546DC1610AD2 (User_id), add constraint FKF587546DC1610AD2 foreign key (User_id) references user (id);
alter table team_user add index FKF587546D745BA992 (Team_id), add constraint FKF587546D745BA992 foreign key (Team_id) references team (id);
alter table worktype add index FK22265CB3872F902 (projectType_id), add constraint FK22265CB3872F902 foreign key (projectType_id) references projecttype (id);