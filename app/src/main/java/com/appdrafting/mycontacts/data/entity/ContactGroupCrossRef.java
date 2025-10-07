package com.appdrafting.mycontacts.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Junction table for many-to-many relationship between Contact and ContactGroup
 * This allows a contact to belong to multiple groups and a group to have multiple contacts
 */
@Entity(
    tableName = "contact_group_cross_ref",
    primaryKeys = {"contact_id", "group_id"},
    foreignKeys = {
        @ForeignKey(
            entity = Contact.class,
            parentColumns = "id",
            childColumns = "contact_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = ContactGroup.class,
            parentColumns = "id",
            childColumns = "group_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "contact_id"),
        @Index(value = "group_id")
    }
)
public class ContactGroupCrossRef {
    
    @ColumnInfo(name = "contact_id")
    public int contactId;
    
    @ColumnInfo(name = "group_id")
    public int groupId;

    public ContactGroupCrossRef(int contactId, int groupId) {
        this.contactId = contactId;
        this.groupId = groupId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}