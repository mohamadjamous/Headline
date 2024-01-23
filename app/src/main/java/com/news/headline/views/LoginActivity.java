package com.news.headline.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.R;
import com.news.headline.db.AppDatabase;
import com.news.headline.db.dao.UserDao;
import com.news.headline.databinding.ActivityLoginBinding;
import com.news.headline.viewmodels.UserViewModel;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding bind;
    private AlertDialog dialog;

    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(this);

        bind.back.setOnClickListener(view -> finish());
        bind.login.setOnClickListener(view -> loginUser(bind.email.getText().toString(), bind.password.getText().toString()));
        bind.signUp.setOnClickListener(view -> {
                    startActivity(new Intent(this, SignupActivity.class));
                    finish();
                }
        );

    }

    private void loginUser(String email, String password) {

        // Validate email and password
        if (TextUtils.isEmpty(email)) {
            // Email is empty
            showToast("Please enter your email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            // Password is empty
            showToast("Please enter your password");
            return;
        }

        initDialog(getString(R.string.logging_in));
        progressState(true);

        viewModel.loginUser(email, password).observe(this, userModel -> {

            if (userModel != null) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CreatePostActivity.class));
                finish();
            } else {
                showToast("Invalid email or password");
            }

            progressState(false);
        });


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initDialog(String message) {
        dialog = CustomProgressDialog.showCustomDialog(this, message, R.color.white);
    }

    //show & hide progress bar
    private void progressState(boolean show) {
        if (show) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

}