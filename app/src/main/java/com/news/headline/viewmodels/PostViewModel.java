package com.news.headline.viewmodels;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.news.headline.repositories.PostsRepo;
import com.news.headline.models.PostModel;

import java.util.List;

public class PostViewModel extends ViewModel {

    Activity activity;
    PostsRepo postsRepo;
    public void init(Activity mContext){
        activity = mContext;
        postsRepo =  PostsRepo.getInstance(activity);
    }

    public MutableLiveData<List<PostModel>> requestToFetchPosts(){
        return postsRepo.fetchPosts();
    }
}