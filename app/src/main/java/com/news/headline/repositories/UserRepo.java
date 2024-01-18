package com.news.headline.repositories;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.news.headline.db.AppDatabase;
import com.news.headline.db.dao.UserDao;
import com.news.headline.db.entities.UserEntity;
import com.news.headline.models.UserModel;
import com.news.headline.utils.Constants;

import java.util.Map;

public class UserRepo {
    private UserDao userDao;
    private FirebaseFirestore db;
    private Context context;

    private static UserRepo instance;

    public static UserRepo getInstance(Activity mContext) {
        if (instance == null) {
            instance = new UserRepo(mContext);
        }

        return instance;
    }


    public UserRepo(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    // fetch user from room database
    public MutableLiveData<UserEntity> getUserFromDatabase() {
        return new MutableLiveData<>(userDao.getUser());
    }


    public MutableLiveData<UserModel> createFirebaseUser(String email, String password, String username, String dob) {

        final MutableLiveData<UserModel>[] mutableLiveData = new MutableLiveData[]{new MutableLiveData<>()};

        // Create a Firebase user account
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a user in the Firebase database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference(Constants.USERS_COLLECTION).child(auth.getCurrentUser().getUid());
                        userRef.setValue(new UserModel(auth.getCurrentUser().toString(), email, password, username, dob));


                        // Create a User object or use the retrieved information as needed
                        UserEntity userEntity = new UserEntity();
                        userEntity.email = email;
                        userEntity.userName = username;
                        userEntity.uid = auth.getCurrentUser().getUid().toString();
                        userEntity.dob = dob;

                        // Insert the user data into your local database or perform any other necessary actions
                        userDao.insertAll(userEntity);

                        UserModel userModel = new UserModel(auth.getCurrentUser().getUid(), email, username, dob);

                        db.collection(Constants.USERS_COLLECTION).add(userModel).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                mutableLiveData[0].setValue(userModel);
                            } else {
                                mutableLiveData[0].setValue(null);
                            }

                        });

                    } else {
                        // Handle the error
                        Exception error = task.getException();
                        Log.e("createUserAccount", error.getMessage(), error);
                        mutableLiveData[0].setValue(null);
                    }
                });
        return mutableLiveData[0];
    }


    /*
     login user via firebase auth
     get user id from firebase auth
     fetch user account from firestore
     save user in room database
     */
    public MutableLiveData<UserModel> loginFirebaseUser(String email, String password) {

        final MutableLiveData<UserModel>[] mutableLiveData = new MutableLiveData[]{new MutableLiveData<>()};

        // Call Firebase Authentication to sign in the user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                // Get the signed-in user
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {

                    // Get additional user information (e.g., username)
                    String uid = firebaseUser.getUid();
                    System.out.println("uidValue: " + uid);

                    // Create a reference to the collection.
                    DocumentReference documentReference = db.collection(Constants.USERS_COLLECTION).document(uid);

                    // Get a snapshot of the collection.
                    documentReference.get().addOnCompleteListener(task1 -> {

                        UserModel userModel;

                        if (task1.isSuccessful()) {

                            // Get the snapshot's document.
                            DocumentSnapshot document = task1.getResult();

                            // Iterate over the document.
                            if (document != null) {

                                userModel = new UserModel(document.getString("uid"), document.getString("email"), document.getString("userName"), document.getString("dob"));


                                // Create a User object or use the retrieved information as needed
                                UserEntity userEntity = new UserEntity();
                                userEntity.email = userModel.email;
                                userEntity.userName = userModel.userName;
                                userEntity.uid = userModel.uid;
                                userEntity.dob = userModel.dob;

                                // Insert the user data into your local database or perform any other necessary actions
                                userDao.insertAll(userEntity);


                                mutableLiveData[0].setValue(userModel);
                            } else {
                                mutableLiveData[0].setValue(null);
                            }

                        } else {

                            // Handle the error.
                            mutableLiveData[0].setValue(null);
                        }
                    });
                }

                mutableLiveData[0].setValue(null);
            } else {
                // If sign in fails, display a message to the user.

                mutableLiveData[0].setValue(null);
            }

        });

        return mutableLiveData[0];
    }


}
