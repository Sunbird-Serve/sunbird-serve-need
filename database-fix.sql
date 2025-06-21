-- Database Schema Fix Script
-- Run this script to fix the software_platform column type

-- Fix the software_platform column in input_parameters table
-- Change from VARCHAR to proper enum handling

-- Option 1: If you want to keep it as VARCHAR (recommended for existing data)
-- This is already correct, just ensure the column allows the enum values

-- Option 2: If you want to convert to a more structured approach
-- ALTER TABLE input_parameters 
-- ALTER COLUMN software_platform TYPE VARCHAR(50);

-- Option 3: Add a check constraint to ensure valid values
ALTER TABLE input_parameters 
ADD CONSTRAINT chk_software_platform 
CHECK (software_platform IN ('GMEET', 'SKYPE', 'WEBEX', 'TEAMS', 'GITHUB', 'GDRIVE', 'ONEDRIVE'));

-- Verify the fix
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'input_parameters' 
AND column_name = 'software_platform'; 