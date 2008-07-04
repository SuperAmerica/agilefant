-- add businessThemes
create table businesstheme (id integer not null auto_increment, name varchar(255) not null unique, description text, primary key (id)) ENGINE=InnoDB;
create table backlogitem_businesstheme (backlogitem_id integer not null, businesstheme_id integer not null, primary key (backlogitem_id, businesstheme_id)) ENGINE=InnoDB;
alter table backlogitem_businesstheme add index FKE72E399AE94683E2 (backlogitem_id), add constraint FKE72E399AE94683E2 foreign key (backlogitem_id) references backlogitem (id);
alter table backlogitem_businesstheme add index FKE72E399AE01C3F02 (businesstheme_id);
-- alter table backlogitem_businesstheme add index FKE72E399AE01C3F02 (businesstheme_id), add constraint FKE72E399AE01C3F02 foreign key (businesstheme_id) references businesstheme (id);

