package com.news.headline.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

public class UserModel {

    public String uid, email, password, userName, dob;


    public UserModel(String uid, String email, String password, String userName, String dob) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.dob = dob;
    }

    public UserModel(String uid, String email, String userName, String dob) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.dob = dob;
    }
}
