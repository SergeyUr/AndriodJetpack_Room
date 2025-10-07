package com.appdrafting.mycontacts.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ContactGroup entity - represents a parent group for contacts
 * This demonstrates a parent-child relationship where ContactGroup is the parent
 * and Contact is the child.
 */
@Entity(tableName = "contact_groups")
public class ContactGroup {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "group_name")
    public String groupName;

    public String description;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public ContactGroup() {
        this.createdAt = System.currentTimeMillis();
    }

    public ContactGroup(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}