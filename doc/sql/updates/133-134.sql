-- Add column initials to User table
ALTER TABLE User ADD COLUMN initials VARCHAR(255);

-- Copy 5 first letters of username to initials
UPDATE User SET initials = SUBSTRING(fullName, 1, 5);
