package com.news.headline.views;

import androidx.appcompat.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding bind;
    private AppDatabase db;
    private UserDao userDao;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());


        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        userDao = db.userDao();

        bind.back.setOnClickListener(view -> finish());

        bind.login.setOnClickListener(view -> {
            loginUser(bind.email.getText().toString(), bind.password.getText().toString());
        });

        bind.signUp.setOnClickListener(view -> startActivity(new Intent(this, SignupActivity.class)));

    }

    private void loginUser(String email, String password) {

        initDialog(getString(R.string.logging_in));
        progressState(true);

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