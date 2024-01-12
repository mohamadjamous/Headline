package com.news.headline.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.headline.db.entities.UserEntity;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.R;
import com.news.headline.db.AppDatabase;
import com.news.headline.db.dao.UserDao;
import com.news.headline.databinding.ActivitySignupBinding;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE_PHOTO = 12;
    private static final int REQUEST_IMAGE_GALLERY = 15;
    private ActivitySignupBinding bind;
    private Context context;
    private Bitmap photo;

    final Calendar myCalendar = Calendar.getInstance();
    private FirebaseAuth firebaseAuth;
    private android.app.AlertDialog dialog;
    private AppDatabase db;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        context = this;

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        userDao = db.userDao();

        firebaseAuth = FirebaseAuth.getInstance();

        bind.back.setOnClickListener(view -> finish());




        bind.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailAddress = editable.toString();

                if (emailAddress.length() != 0) {
                    bind.username.setText(createUsername(emailAddress));
                }

            }
        });


        bind.dob.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateLabel();
                }
            };
            bind.dob.setOnClickListener(view1 ->
                    new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        });

        bind.createAccount.setOnClickListener(view -> {

            createUser();
        });

    }


    private void updateLabel() {
        bind.dob.setText(new SimpleDateFormat("MM/dd/yy", Locale.US).format(myCalendar.getTime()));
    }

    private void createUser() {

        initDialog(getString(R.string.creating_your_account));
        progressState(true);
        String email = bind.email.getText().toString();
        String password = bind.password.getText().toString();
        String userName = bind.username.getText().toString();
        String dateOfBirth = bind.dob.getText().toString();


        // Check if email is empty or null
        if (email.trim().isEmpty()) {
            showToast(context, "Please enter a valid email");
            progressState(false);
            return;
        }

        // Check if password is empty or null
        if (password.trim().isEmpty()) {
            showToast(context, "Please enter a valid password");
                        progressState(false); return;
        }

        // Check if username is empty or null
        if (userName.trim().isEmpty()) {
            showToast(context, "Please enter a valid username");
                        progressState(false); return;
        }

        // Check if dateOfBirth is empty or null
        if (dateOfBirth.trim().isEmpty()) {
            showToast(context, "Please enter a valid date of birth");
                        progressState(false); return;
        }

        // All inputs are valid


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Save user information to Firestore


                            saveUserToFirestore(user.getUid(), userName, dateOfBirth, email, password);


                        }
                    } else {
                        // User registration failed
                        showToast(this, "Registration failed: " + task.getException().getMessage());
                        System.out.println("ErrorMessage: " + task.getException().getMessage());
                        progressState(false);

                    }
                });


    }

    private void saveUserToFirestore(String userId, String userName, String dateOfBirth, String email, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        Map<String, Object> user = new HashMap<>();
        user.put("emailAddress", email);
        user.put("userName", userName);
        user.put("password", password);
        user.put("dateOfBirth", dateOfBirth);

        usersRef.document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // User information saved successfully
                    showToast(this, "User registered successfully!");
                    UserEntity user1 = new UserEntity();
                    user1.email = email;
                    user1.userName = userName;
                    user1.uid = userId;
                    userDao.insertAll(user1);
                    startActivity(new Intent(SignupActivity.this, CreatePostActivity.class));
                    progressState(false);
                })
                .addOnFailureListener(e -> {
                    // User information failed to save
                    showToast(this, "Failed to save user information: " + e.getMessage());
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

    public String createUsername(String fullName) {
        // Split the full name into first and last names
        String[] names = fullName.split(" ");

        // Check if there are at least two parts (first name and last name)
        if (names.length >= 2) {
            // Concatenate the first letter of the first name with the last name
            String username = names[0].substring(0, 1).toLowerCase() + names[1].toLowerCase();

            // Add a random number to the username
            Random random = new Random();
            int randomNumber = random.nextInt(100); // Change 100 to your desired range
            username += randomNumber;

            return username;
        } else {
            // If there are not enough parts, return an empty string or handle the error accordingly
            return "";
        }
    }









}