-- Add global field to global themes
alter table businesstheme add column `global` boolean default 0;
