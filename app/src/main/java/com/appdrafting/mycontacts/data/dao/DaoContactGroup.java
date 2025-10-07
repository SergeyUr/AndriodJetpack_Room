package com.appdrafting.mycontacts.data.dao;

import androidx.room.*;
import com.appdrafting.mycontacts.data.entity.ContactGroup;
import com.appdrafting.mycontacts.data.entity.ContactGroupCrossRef;
import com.appdrafting.mycontacts.data.entity.ContactWithGroup;

import java.util.List;

/**
 * DAO for ContactGroup operations
 * Demonstrates parent-child relationship queries
 */
@Dao
public interface DaoContactGroup {

    // ========== ContactGroup CRUD Operations ==========
    
    @Insert
    long addContactGroup(ContactGroup group);

    @Update
    void updateContactGroup(ContactGroup group);

    @Delete
    void deleteContactGroup(ContactGroup group);

    @Query("SELECT * FROM contact_groups")
    List<ContactGroup> getAllContactGroups();

    @Query("SELECT * FROM contact_groups WHERE id = :groupId")
    ContactGroup getContactGroup(int groupId);

    // ========== Parent-Child Relationship Queries ==========
    
    /**
     * Get a ContactGroup with all its associated contacts
     * This demonstrates the parent-child relationship
     */
    @Transaction
    @Query("SELECT * FROM contact_groups WHERE id = :groupId")
    ContactWithGroup getContactGroupWithContacts(int groupId);

    /**
     * Get all ContactGroups with their associated contacts
     */
    @Transaction
    @Query("SELECT * FROM contact_groups")
    List<ContactWithGroup> getAllContactGroupsWithContacts();

    /**
     * Get groups that have at least one contact
     */
    @Query("SELECT DISTINCT cg.* FROM contact_groups cg " +
           "INNER JOIN contacts c ON cg.id = c.group_id")
    List<ContactGroup> getGroupsWithContacts();

    /**
     * Count contacts in a specific group
     */
    @Query("SELECT COUNT(*) FROM contacts WHERE group_id = :groupId")
    int getContactCountInGroup(int groupId);

    // ========== Many-to-Many Relationship Operations ==========
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContactGroupCrossRef(ContactGroupCrossRef crossRef);

    @Delete
    void deleteContactGroupCrossRef(ContactGroupCrossRef crossRef);

    /**
     * Get all group IDs for a specific contact (many-to-many)
     */
    @Query("SELECT group_id FROM contact_group_cross_ref WHERE contact_id = :contactId")
    List<Integer> getGroupIdsForContact(int contactId);

    /**
     * Get all contact IDs for a specific group (many-to-many)
     */
    @Query("SELECT contact_id FROM contact_group_cross_ref WHERE group_id = :groupId")
    List<Integer> getContactIdsForGroup(int groupId);

    /**
     * Remove all contacts from a group (many-to-many)
     */
    @Query("DELETE FROM contact_group_cross_ref WHERE group_id = :groupId")
    void removeAllContactsFromGroup(int groupId);

    /**
     * Remove contact from all groups (many-to-many)
     */
    @Query("DELETE FROM contact_group_cross_ref WHERE contact_id = :contactId")
    void removeContactFromAllGroups(int contactId);
}