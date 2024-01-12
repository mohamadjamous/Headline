package com.news.headline.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "posts")
public class PostEntity {


    @PrimaryKey
    public int primaryId;

    @ColumnInfo(name = "postId")
    public String postId;


    @ColumnInfo(name = "photoUrl")
    public String photoUrl;

    @ColumnInfo (name = "description")
    public String description;

    @ColumnInfo (name = "title")
    public String title;

    @ColumnInfo (name = "userName")
    public String userName;

    @ColumnInfo (name = "date")
    public Date date;


}
