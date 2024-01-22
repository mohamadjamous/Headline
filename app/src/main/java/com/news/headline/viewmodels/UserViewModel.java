package com.news.headline.viewmodels;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.news.headline.db.entities.UserEntity;
import com.news.headline.models.PostModel;
import com.news.headline.models.UserModel;
import com.news.headline.repositories.UserRepo;

import java.util.List;

// UserViewModel.java
public class UserViewModel extends ViewModel {
    Activity activity;
    UserRepo userRepo;
    public void init(Activity mContext){
        activity = mContext;
        userRepo =  UserRepo.getInstance(activity);
    }

    public MutableLiveData<UserEntity> fetchUserFromDatabase(){
        return userRepo.getUserFromDatabase();
    }

    public MutableLiveData<UserModel> loginUser(String email, String password){
        return userRepo.loginFirebaseUser(email, password);
    }

    public void logoutUser(UserEntity user){
        userRepo.logout(user);
    }


    public MutableLiveData<UserModel> createUser(String email, String password, String username, String dob){
        return userRepo.createFirebaseUser(email, password, username, dob);
    }


}
