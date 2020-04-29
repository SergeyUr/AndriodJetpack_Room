package com.appdrafting.mycontacts.data.dao;

import androidx.room.*;
import com.appdrafting.mycontacts.data.entity.Contact;

import java.util.List;

@Dao
public interface DaoContact {

    @Insert
    public long addContact(Contact pContact);

    @Update
    public void updateContact(Contact pContact);

    @Delete
    public void deleteContact(Contact pContact);

    @Query("select * from contacts")
    public List<Contact> getAllContacts();

    @Query("select * from contacts where id ==:pContactId ")
    public Contact getContact(long pContactId);
}
