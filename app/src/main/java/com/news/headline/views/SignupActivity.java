package com.news.headline.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.news.headline.R;
import com.news.headline.databinding.ActivitySignupBinding;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.viewmodels.UserViewModel;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {


    private ActivitySignupBinding binding;
    private Context context;
    final Calendar myCalendar = Calendar.getInstance();
    private android.app.AlertDialog dialog;

    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(this);


        binding.back.setOnClickListener(view -> finish());


        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailAddress = editable.toString();

                if (!emailAddress.isEmpty()) {
                    binding.username.setText(createUserName(emailAddress));
                }

            }
        });


        binding.dob.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener date = (view12, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            };
            binding.dob.setOnClickListener(view1 ->
                    new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        });


        binding.dobEditText.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener date = (view12, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            };
            binding.dobEditText.setOnClickListener(view1 ->
                    new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        });

        binding.createAccount.setOnClickListener(view -> createUser());

    }


    private void updateLabel() {
        binding.dobEditText.setText(new SimpleDateFormat("MM/dd/yy", Locale.US).format(myCalendar.getTime()));
    }

    private void createUser() {

        initDialog(getString(R.string.creating_your_account));
        progressState(true);
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String userName = binding.username.getText().toString();
        String dateOfBirth = binding.dobEditText.getText().toString();


        // Check if email is empty or null
        if (email.trim().isEmpty()) {
            showToast(context, "Please enter a valid email");
            progressState(false);
            return;
        }

        // Check if password is empty or null
        if (password.trim().isEmpty()) {
            showToast(context, "Please enter a valid password");
            progressState(false);
            return;
        }

        // Check if username is empty or null
        if (userName.trim().isEmpty()) {
            showToast(context, "Please enter a valid username");
            progressState(false);
            return;
        }

        // Check if dateOfBirth is empty or null
        if (dateOfBirth.trim().isEmpty()) {
            showToast(context, "Please enter a valid date of birth");
            progressState(false);
            return;
        }

        // All inputs are valid
        viewModel.createUser(email, password, userName, dateOfBirth).observe(this, userModel ->
        {
            if (userModel != null) {
                Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CreatePostActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
            }
            progressState(false);
        });


    }


    // Helper method to show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void initDialog(String message) {
        dialog = CustomProgressDialog.showCustomDialog(context, message, R.color.white);
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

    // Create a method called "createUserName" that takes email address as String and to add a random number and returns String
    public static String createUserName(String email) {
        // Remove ".com" and anything after it from the email address
        if (email != null && email.contains(".com")) {
            String username = email.substring(0, email.indexOf(".com") + 1);

            // Add a random number to the username
            Random random = new Random();
            int randomNumber = random.nextInt(1000);

            // Add the random number to the username
            username = username + randomNumber;

            // Return the username
            return username;
        }
        return null;
    }


}