package com.appdrafting.mycontacts.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

/**
 * Data class representing a ContactGroup with all its associated contacts
 * This uses Room's @Relation annotation to automatically fetch the parent-child relationship
 */
public class ContactWithGroup {
    
    @Embedded
    public ContactGroup group;
    
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    public List<Contact> contacts;

    public ContactGroup getGroup() {
        return group;
    }

    public void setGroup(ContactGroup group) {
        this.group = group;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}