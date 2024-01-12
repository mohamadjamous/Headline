package com.news.headline.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

public class UserModel {

    public String uid;
    public String email;
    public String photo;
    public String userName;


    public UserModel(String uid, String email, String photo, String userName) {
        this.uid = uid;
        this.email = email;
        this.photo = photo;
        this.userName = userName;
    }
}
