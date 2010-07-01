-- User
INSERT INTO users(id,loginName,enabled) VALUES (1,'Testuser',true);
INSERT INTO users(id,loginName,enabled) VALUES (2,'User with no collections',true);

-- Widget collections
INSERT INTO widgetcollections(id,name,user_id) VALUES (1,'B: First',null);
INSERT INTO widgetcollections(id,name,user_id) VALUES (2,'A: Second',null);
INSERT INTO widgetcollections(id,name,user_id) VALUES (3,'C: User first',1);
