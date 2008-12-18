-- Add status-field to project. All old projects have OK-status.
alter table backlog add column status integer;
update backlog set status = 0 where product_id is not null;