# Database Migration Guide - Adding Parent Relations

This guide explains how to add parent-child relations to the contacts database.

## Overview

Three approaches are provided for implementing parent relations:

1. **Self-Referencing**: Contacts can have a parent contact
2. **Separate Parent Table**: ContactGroup as parent, Contact as child
3. **Many-to-Many**: Contacts can belong to multiple groups

## Implementation Steps

### Step 1: Update Contact Entity (if using group_id approach)

Add the foreign key relationship to `Contact.java`:

```java
@Entity(
    tableName = "contacts",
    foreignKeys = @ForeignKey(
        entity = ContactGroup.class,
        parentColumns = "id",
        childColumns = "group_id",
        onDelete = ForeignKey.SET_NULL
    ),
    indices = {@Index(value = "group_id")}
)
public class Contact {
    // ... existing fields ...
    
    @ColumnInfo(name = "group_id")
    public Integer groupId;  // Nullable - contact may not belong to any group
    
    // ... rest of the class ...
}
```

### Step 2: Update AppDatabase

Update `AppDatabase.java` to include new entities and increment version:

```java
@Database(
    entities = {
        Contact.class,
        ContactGroup.class,
        ContactGroupCrossRef.class  // If using many-to-many
    },
    version = 2  // Increment version number
)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract DaoContact getDaoContact();
    public abstract DaoContactGroup getDaoContactGroup();
    
    // Migration from version 1 to version 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create contact_groups table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS contact_groups (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "group_name TEXT NOT NULL, " +
                "description TEXT, " +
                "created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000))"
            );
            
            // Add group_id column to contacts
            database.execSQL(
                "ALTER TABLE contacts ADD COLUMN group_id INTEGER DEFAULT NULL"
            );
            
            // Create index
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_contacts_group_id ON contacts(group_id)"
            );
            
            // If using many-to-many, also create junction table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS contact_group_cross_ref (" +
                "contact_id INTEGER NOT NULL, " +
                "group_id INTEGER NOT NULL, " +
                "PRIMARY KEY (contact_id, group_id), " +
                "FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (group_id) REFERENCES contact_groups(id) ON DELETE CASCADE)"
            );
            
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_contact_group_cross_ref_contact_id " +
                "ON contact_group_cross_ref(contact_id)"
            );
            
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_contact_group_cross_ref_group_id " +
                "ON contact_group_cross_ref(group_id)"
            );
        }
    };
}
```

### Step 3: Update Database Instance Creation

In your MainActivity or Application class, add the migration:

```java
AppDatabase db = Room.databaseBuilder(
    getApplicationContext(),
    AppDatabase.class,
    "contacts-db"
)
.addMigrations(AppDatabase.MIGRATION_1_2)
.build();
```

## Usage Examples

### Creating a Contact Group

```java
ContactGroup familyGroup = new ContactGroup("Family", "Family members");
long groupId = db.getDaoContactGroup().addContactGroup(familyGroup);
```

### Assigning Contact to Group

```java
Contact contact = db.getDaoContact().getContact(contactId);
contact.groupId = groupId;
db.getDaoContact().updateContact(contact);
```

### Getting All Contacts in a Group

```java
ContactWithGroup groupWithContacts = db.getDaoContactGroup()
    .getContactGroupWithContacts(groupId);
List<Contact> contacts = groupWithContacts.contacts;
```

### Many-to-Many: Add Contact to Multiple Groups

```java
// Add contact to Family group
db.getDaoContactGroup().insertContactGroupCrossRef(
    new ContactGroupCrossRef(contactId, familyGroupId)
);

// Add same contact to Friends group
db.getDaoContactGroup().insertContactGroupCrossRef(
    new ContactGroupCrossRef(contactId, friendsGroupId)
);
```

## SQL Script

The complete SQL script is available in `app/src/main/assets/parent_relation_schema.sql`.
This file contains all the SQL commands and example queries for reference.

## Testing

After implementing the changes:

1. Uninstall the app or clear app data
2. Install and run the app with the new database version
3. Or, keep existing data by implementing the migration (recommended)

## Foreign Key Benefits

- **Referential Integrity**: Cannot assign invalid group_id
- **Cascade Deletes**: When using CASCADE, deleting a group removes all associations
- **Data Consistency**: Database enforces relationship rules automatically