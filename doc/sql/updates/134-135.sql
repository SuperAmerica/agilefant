-- Remove links to iteration goals if the Backlog Item is in an other backlog
update BacklogItem, IterationGoal set iterationGoal_id = null where BacklogItem.iterationGoal_id = IterationGoal.id and BacklogItem.backlog_id != IterationGoal.iteration_id;

