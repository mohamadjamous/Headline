package com.news.headline.models;

import java.io.Serializable;
import java.util.Date;

public class PostModel implements Serializable {


    public String photoUrl, description, title, userName, date;
    public PostModel(String photoUrl, String description, String title, String userName, String date) {
        this.photoUrl = photoUrl;
        this.description = description;
        this.title = title;
        this.userName = userName;
        this.date = date;
    }
}
