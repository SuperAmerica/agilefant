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

-- Add History and BacklogHistory
alter table Backlog add column history_fk integer;
create table History (DTYPE integer not null, id integer not null auto_increment, primary key (id)) ENGINE=InnoDB;
create table HistoryEntry (id integer not null auto_increment, effortLeft integer, originalEstimate integer, date date, history_id integer, primary key (id)) ENGINE=InnoDB;
alter table Backlog add index FK4E86B8DDC91A641F (history_fk), add constraint FK4E86B8DDC91A641F foreign key (history_fk) references History (id);
alter table HistoryEntry add index FK9367445EC91A6475 (history_id), add constraint FK9367445EC91A6475 foreign key (history_id) references History (id);
alter table HistoryEntry add index FK9367445EFD7DC542 (history_id), add constraint FK9367445EFD7DC542 foreign key (history_id) references History (id);


-- UPDATE BACKLOG HISTORY

-- Create procedure for update process
DROP PROCEDURE IF EXISTS updateHistory;
DELIMITER $$

CREATE PROCEDURE updateHistory()
BEGIN
DECLARE done INT DEFAULT 0;
DECLARE backlogId INT DEFAULT 0;
declare historyId INT;
DECLARE cur1 CURSOR FOR select id from Backlog;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
open cur1;
repeat
	fetch cur1 into backlogId;
	insert into History (DTYPE) values(1);
	set historyId = LAST_INSERT_ID();
	update Backlog bl set history_fk=historyId where bl.id=backlogId;
	replace into HistoryEntry (effortLeft, originalEstimate, date, history_id) (select sum(effortLeft), sum(originalEstimate), CURRENT_DATE(), historyId from BacklogItem where backlog_id=backlogId);
until done end repeat;

close cur1;
end $$

delimiter ;

CALL updateHistory();

-- Drop the procedure
drop procedure updateHistory;

-- Insert effort history entries to HistoryEntry table, skip items for this date because they were already created in stored procedure
insert into HistoryEntry (effortLeft, originalEstimate, date, history_id) (select eh.effortLeft,eh.originalEstimate, eh.date, bl.history_fk from EffortHistory eh, Backlog bl where eh.backlog_id=bl.id and eh.date != CURRENT_DATE());


-- UPDATE STATUSES TO INCLUDE PENDING STATUS
UPDATE Task SET status=status+1 WHERE status > 1;
UPDATE BacklogItem SET status=status+1 WHERE status > 1;

-- Create state fields (will replace status)
alter table BacklogItem add column state integer;
alter table Task add column state integer;

-- Copy Task and Backlog status to state
UPDATE Task SET state = status;
UPDATE BacklogItem SET state = status;

-- Drop Status fields - IterationGoal's status is unused
ALTER TABLE Task DROP COLUMN `status`;
ALTER TABLE BacklogItem DROP COLUMN `status`;
ALTER TABLE IterationGoal DROP COLUMN `status`;


