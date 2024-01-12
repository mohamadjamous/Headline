package com.news.headline.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.news.headline.db.entities.UserEntity;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    LiveData<UserEntity> getUser();

    @Insert
    void insertAll(UserEntity...users);

    @Delete
    void delete(UserEntity user);



}
