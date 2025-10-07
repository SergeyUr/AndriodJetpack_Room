-- SQL Script for Parent-Child Relations in Contacts Database
-- This script demonstrates two approaches for implementing parent relations

-- ============================================================================
-- APPROACH 1: Self-Referencing Parent Relation
-- Contacts can have a parent contact (hierarchical structure)
-- ============================================================================

-- Add parent_id column to existing contacts table
ALTER TABLE contacts ADD COLUMN parent_id INTEGER DEFAULT NULL;

-- Add foreign key constraint to enforce parent relation
-- Note: Room will handle this with @ForeignKey annotation in the Entity
-- This ensures referential integrity - parent_id must reference an existing contact
CREATE INDEX IF NOT EXISTS index_contacts_parent_id ON contacts(parent_id);

-- Example queries for self-referencing parent relation:
-- 
-- Get all child contacts of a specific parent:
-- SELECT * FROM contacts WHERE parent_id = ?
--
-- Get parent of a contact:
-- SELECT p.* FROM contacts c 
-- INNER JOIN contacts p ON c.parent_id = p.id 
-- WHERE c.id = ?
--
-- Get all top-level contacts (no parent):
-- SELECT * FROM contacts WHERE parent_id IS NULL


-- ============================================================================
-- APPROACH 2: Separate Parent Table (Contact Groups)
-- Create a ContactGroup table as parent, with contacts as children
-- ============================================================================

-- Create contact_groups table
CREATE TABLE IF NOT EXISTS contact_groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    group_name TEXT NOT NULL,
    description TEXT,
    created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)
);

-- Add group_id to contacts table to link with parent group
ALTER TABLE contacts ADD COLUMN group_id INTEGER DEFAULT NULL;

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS index_contacts_group_id ON contacts(group_id);

-- Example queries for group-based parent relation:
--
-- Get all contacts in a specific group:
-- SELECT c.* FROM contacts c WHERE c.group_id = ?
--
-- Get group information with contact count:
-- SELECT g.*, COUNT(c.id) as contact_count 
-- FROM contact_groups g 
-- LEFT JOIN contacts c ON g.id = c.group_id 
-- GROUP BY g.id
--
-- Get contacts with their group information:
-- SELECT c.*, g.group_name, g.description 
-- FROM contacts c 
-- LEFT JOIN contact_groups g ON c.group_id = g.id


-- ============================================================================
-- APPROACH 3: Many-to-Many Relation (Contact can belong to multiple groups)
-- Using a junction table
-- ============================================================================

-- Create junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS contact_group_cross_ref (
    contact_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    PRIMARY KEY (contact_id, group_id),
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);

-- Create indices for junction table
CREATE INDEX IF NOT EXISTS index_contact_group_cross_ref_contact_id 
    ON contact_group_cross_ref(contact_id);
CREATE INDEX IF NOT EXISTS index_contact_group_cross_ref_group_id 
    ON contact_group_cross_ref(group_id);

-- Example queries for many-to-many relation:
--
-- Get all groups for a specific contact:
-- SELECT g.* FROM contact_groups g
-- INNER JOIN contact_group_cross_ref cgc ON g.id = cgc.group_id
-- WHERE cgc.contact_id = ?
--
-- Get all contacts in a specific group:
-- SELECT c.* FROM contacts c
-- INNER JOIN contact_group_cross_ref cgc ON c.id = cgc.contact_id
-- WHERE cgc.group_id = ?
--
-- Add contact to group:
-- INSERT INTO contact_group_cross_ref (contact_id, group_id) VALUES (?, ?)


-- ============================================================================
-- Sample Data for Testing
-- ============================================================================

-- Insert sample groups
INSERT INTO contact_groups (group_name, description) VALUES 
    ('Family', 'Family members and relatives'),
    ('Work', 'Work colleagues and business contacts'),
    ('Friends', 'Personal friends');

-- Note: To link existing contacts to groups, you would run:
-- UPDATE contacts SET group_id = 1 WHERE id IN (1, 2, 3); -- Assign to Family group
-- UPDATE contacts SET group_id = 2 WHERE id IN (4, 5); -- Assign to Work group

-- For many-to-many relationships:
-- INSERT INTO contact_group_cross_ref (contact_id, group_id) VALUES (1, 1), (1, 3);
-- This allows contact 1 to be in both Family and Friends groups


-- ============================================================================
-- Cleanup (for testing purposes)
-- ============================================================================

-- To remove the parent relation modifications:
-- 
-- For self-referencing approach:
-- ALTER TABLE contacts DROP COLUMN parent_id;
--
-- For group-based approach:
-- DROP TABLE IF EXISTS contact_groups;
-- ALTER TABLE contacts DROP COLUMN group_id;
--
-- For many-to-many approach:
-- DROP TABLE IF EXISTS contact_group_cross_ref;