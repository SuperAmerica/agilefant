create table story_access (id integer not null auto_increment, date datetime, story_id integer not null, user_id integer not null, primary key (id)) ENGINE=InnoDB;
alter table users add column recentItemsNumberOfWeeks integer not null;
alter table story_access add index FK44C5ABEEE0E4BFA2 (story_id), add constraint FK44C5ABEEE0E4BFA2 foreign key (story_id) references stories (id);
alter table story_access add index FK44C5ABEEC1610AD2 (user_id), add constraint FK44C5ABEEC1610AD2 foreign key (user_id) references users (id);
