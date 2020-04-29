package com.appdrafting.mycontacts.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    public String email;

    public String phone;

    public Contact() {
    }

    @Ignore
    public Contact(String pFirstName, String pLastName, String pEmail, String pPhone) {
        firstName = pFirstName;
        lastName = pLastName;
        email = pEmail;
        phone = pPhone;
    }

    @Ignore
    public Contact(int pId, String pFirstName, String pLastName, String pEmail, String pPhone) {
        id = pId;
        firstName = pFirstName;
        lastName = pLastName;
        email = pEmail;
        phone = pPhone;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String pFirstName) {
        firstName = pFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String pLastName) {
        lastName = pLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String pPhone) {
        phone = pPhone;
    }
}
