package com.news.headline.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.news.headline.db.entities.PostEntity;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts")
    List<PostEntity> getAllPosts();

    @Query("SELECT * FROM posts WHERE postId IN (:postsIds)")
    List<PostEntity> loadAllByIds(int[] postsIds);

    @Query("SELECT * FROM posts WHERE username LIKE :username")
    List<PostEntity> findByUsername(String username);

    @Insert
    void insertAll(PostEntity...users);

    @Delete
    void delete(PostEntity user);



}
