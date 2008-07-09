-- Fixes some very old legacy issues with the placeholder task logics

INSERT INTO Task (backlogItem_id) 
SELECT id FROM BacklogItem WHERE placeHolder_id IS NULL;

UPDATE BacklogItem b, Task t
SET b.placeHolder_id = t.id
WHERE b.id = t.backlogItem_id;

UPDATE Task SET created = now(), effortEstimate = NULL, status = 0, name = 'Placeholder', priority = NULL, description = 'PlaceholderFix', creator_id = 1 WHERE name IS NULL;
