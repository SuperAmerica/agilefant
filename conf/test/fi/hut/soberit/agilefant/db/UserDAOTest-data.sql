INSERT INTO teams (id, name) VALUES (1, 'team1');
INSERT INTO teams (id, name) VALUES (2, 'team2');
INSERT INTO users (id, enabled, loginname, recentItemsNumberOfWeeks) VALUES (1, true, 'user1', 1);
INSERT INTO team_user (User_id, Team_id) VALUES (1,1);
INSERT INTO team_user (User_id, Team_id) VALUES (1,2);
INSERT INTO users (id, enabled, loginname, recentItemsNumberOfWeeks) VALUES (2, true, 'user2', 1);
INSERT INTO users (id, enabled, loginname, recentItemsNumberOfWeeks) VALUES (3, false, 'user3', 1);
INSERT INTO users (id, enabled, loginname, fullName, recentItemsNumberOfWeeks) VALUES (4, true, 'user4', 'Has full name', 1);
