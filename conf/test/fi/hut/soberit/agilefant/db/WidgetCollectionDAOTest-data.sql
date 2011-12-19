-- User
INSERT INTO users(id, loginName, enabled, recentItemsNumberOfWeeks) VALUES (1, 'Testuser', true, 1);
INSERT INTO users(id, loginName, enabled, recentItemsNumberOfWeeks) VALUES (2, 'User with no collections', true, 1);

-- Widget collections
INSERT INTO widgetcollections(id,name,user_id) VALUES (1,'B: First',null);
INSERT INTO widgetcollections(id,name,user_id) VALUES (2,'A: Second',null);
INSERT INTO widgetcollections(id,name,user_id) VALUES (3,'C: User first',1);
