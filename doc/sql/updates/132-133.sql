-- Add column project_id to Backlog table
ALTER TABLE Backlog ADD COLUMN project_id int(11) DEFAULT NULL AFTER deliverable_id;

-- Update type Deliverable to Backlog
UPDATE Backlog SET backlogtype='Project' where backlogtype='Deliverable';

-- Copy deliverable_id to project_id field.
UPDATE Backlog SET project_id = deliverable_id where backlogtype='Iteration';

-- Add foreign key and constraints for project_id
ALTER TABLE Backlog ADD CONSTRAINT `FK4E86B8DDCA187B22` FOREIGN KEY (`project_id`) REFERENCES `Backlog` (`id`) ON DELETE CASCADE;

-- Drop foreign key and constraints for deliverable_id
-- DROP CONSTRAINT `FK4E86B8DD5600C562` FOREIGN KEY (`deliverable_id`) REFERENCES `Backlog` (`id`) ON DELETE CASCADE,
ALTER TABLE Backlog DROP FOREIGN KEY `FK4E86B8DD5600C562`;

-- Drop column deliverable_id
ALTER TABLE Backlog DROP COLUMN `deliverable_id`;

alter table BacklogItem add column effortLeft integer;
alter table BacklogItem add column originalEstimate integer;

-- Update originalEstimes for Backlog items by getting them from task events

drop table if exists bliWithPlaceholder;
drop table if exists blisWithPlaceholderOriginalEstimate;
drop table if exists minEstimateHistoryEventForTask;
drop table if exists estimateHistory;
drop table if exists minEstimateHistoryEventForTaskWithBliIds;
drop table if exists taskSumOriginalEstimates;
drop table if exists blisWithPlaceholderOriginalEstimate;

-- Create temporary table with backlogitems and its placeholder data

create temporary table bliWithPlaceholder (select bli.id as bli_id, bli.placeHolder_id as placeholder_id, t.effortEstimate as effortLeft, t.status as status from BacklogItem bli,Task t where t.id=bli.placeHolder_id);

-- Update BacklogItem effort lefts and statuses with placeholders' correspondants

update BacklogItem bli,bliWithPlaceholder bp set bli.effortLeft = bp.effortLeft, bli.status = bp.status where bp.bli_id=bli.id;

-- Create table with estimate history events, excluding NULL estimates 

create temporary table estimateHistory (select * from TaskEvent where eventType="EstimateHistoryEvent" and (newEstimate IS NOT NULL));

-- Create table with minimum event ids for each task

create temporary table minEstimateHistoryEventForTask (select MIN(estimateHistory.id) as min_event_id, task_id from estimateHistory group by (task_id));

-- Create table with BLIs with their original estimate from placeholder's first EstimateHistoryEvent

create temporary table blisWithPlaceholderOriginalEstimate (select tt.*, eh.newEstimate as originalEstimate from bliWithPlaceholder tt, minEstimateHistoryEventForTask me, estimateHistory eh where tt.placeholder_id = me.task_id and eh.id=me.min_event_id);

-- Update BLIs with placeholder original estimate

update BacklogItem bli,blisWithPlaceholderOriginalEstimate tt set bli.originalEstimate=tt.originalEstimate where bli.id=tt.bli_id;

-- Delete foreign key from BacklogItem referencing placeholder.
alter table BacklogItem drop foreign key `FKC8B7F190622879E4`;

-- Delete unneeded placeholder tasks
delete from Task using Task, BacklogItem where BacklogItem.placeHolder_id=Task.id;

-- UPDATE original estimates for data which has no estimates in placeholder but is calculated based on the Tasks sum 

-- Create temporary table of minEstimateHistoryEventForTask with BLI id

create temporary table minEstimateHistoryEventForTaskWithBliIds (select t.backlogItem_id as bli_id, eht.* from Task t, minEstimateHistoryEventForTask eht  where t.id=eht.task_id);

-- Create temporary table with task original estimates

create temporary table taskOriginalEstimate (select eht.*,event.newEstimate as originalEstimate from TaskEvent event, minEstimateHistoryEventForTaskWithBliIds eht  where event.id=eht.min_event_id);

-- Create temporary table with task sum original estimates

create temporary table taskSumOriginalEstimates (select eht.bli_id, SUM(eht.originalEstimate) as originalEstimate from taskOriginalEstimate eht group by eht.bli_id);

-- Add effort left from task sum to backlog items with effort estimate from place holder

-- update BacklogItem bli, taskSumOriginalEstimates tse set bli.originalEstimate=(bli.originalEstimate+tse.originalEstimate) where bli.originalEstimate IS NOT NULL and tse.bli_id = bli.id;

-- Update effort left to be original effort if effort left is null

update BacklogItem bli, taskSumOriginalEstimates tse set bli.originalEstimate=tse.originalEstimate where bli.originalEstimate IS NULL and tse.bli_id = bli.id;

-- Update all NULL effort estimates to 0

update BacklogItem bli set bli.originalEstimate=0 where bli.originalEstimate IS NULL;

-- Update all NULL effort estimates to original estimate value

update BacklogItem bli set bli.effortLeft=bli.originalEstimate where bli.effortLeft is NULL; 



-- Drop placeHolder_id column
-- DO NOT DROP placeholder id to keep traceability back to event history
-- alter table BacklogItem drop column placeHolder_id;

-- Cleanup temporary tables
drop table bliWithPlaceholder;
drop table blisWithPlaceholderOriginalEstimate;
drop table minEstimateHistoryEventForTask;
drop table estimateHistory;
drop table minEstimateHistoryEventForTaskWithBliIds;
drop table taskSumOriginalEstimates;



-- Drop effort estimate from Task
alter table Task drop column effortEstimate;

-- Drop unneeded Practice tables
drop table if exists PracticeAllocation;
drop table if exists Practice;
drop table if exists PracticeTemplate;





