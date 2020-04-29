package com.appdrafting.mycontacts.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.appdrafting.mycontacts.data.dao.DaoContact;
import com.appdrafting.mycontacts.data.entity.Contact;

@Database(
        entities = {
                Contact.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DaoContact getDaoContact();

}
