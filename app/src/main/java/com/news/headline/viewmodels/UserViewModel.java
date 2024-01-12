package com.news.headline.viewmodels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.news.headline.repositories.UserRepository;

// UserViewModel.java
public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel(Context context) {
        userRepository = new UserRepository(context);
    }

}
