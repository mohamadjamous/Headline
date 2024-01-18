package com.news.headline.db.entities;

import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo (name = "email")
    public String email;

    @ColumnInfo (name = "username")
    public String userName;

    @ColumnInfo (name = "dob")
    public String dob;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
