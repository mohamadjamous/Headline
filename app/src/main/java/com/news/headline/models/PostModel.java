package com.news.headline.models;

import java.util.Date;

public class PostModel {


    public String photoUrl, description, title, userName, date;
    public PostModel(String photoUrl, String description, String title, String userName, String date) {
        this.photoUrl = photoUrl;
        this.description = description;
        this.title = title;
        this.userName = userName;
        this.date = date;
    }
}
