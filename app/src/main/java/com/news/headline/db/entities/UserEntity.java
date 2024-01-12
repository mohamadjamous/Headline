package com.news.headline.db.entities;

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

    @ColumnInfo (name = "photo")
    public String photo;

    @ColumnInfo (name = "username")
    public String userName;

    @ColumnInfo (name = "dob")
    public Date dob;

}
