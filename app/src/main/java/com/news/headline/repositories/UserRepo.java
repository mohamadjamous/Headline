package com.news.headline.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.headline.db.AppDatabase;
import com.news.headline.db.entities.UserEntity;
import com.news.headline.db.dao.UserDao;
import com.news.headline.models.UserModel;
import com.news.headline.utils.Constants;

import java.util.List;
import java.util.Map;

public class UserRepository {
    private UserDao userDao;
    private FirebaseFirestore db;
    private Context context;


    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    // fetch user from room database
    public LiveData<UserEntity> getUser() {
        return userDao.getUser();
    }



    /*
    login user via firebase auth
     get user id from firebase auth
     */
    public MutableLiveData<String> loginFirebaseUser(String email, String password) {

        final MutableLiveData[] messageData = new MutableLiveData[]{new MutableLiveData<>()};

        // Call Firebase Authentication to sign in the user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                // Get the signed-in user
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {

                    // Get additional user information (e.g., username)
                    String uid = firebaseUser.getUid();
                    fetchUserFromFireStore(uid);
                }

                messageData[0].postValue("login successful");
            } else {
                // If sign in fails, display a message to the user.

                messageData[0].postValue("authentication failed");
            }

        });

        return messageData[0];
    }



    /*
    fetch user account from firestore
    save user to room database
     */
    public LiveData<UserModel>  fetchUserFromFireStore(String userId)
    {

        MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();

        // Create a FirebaseFireStore instance.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the collection.
        CollectionReference collectionReference = db.collection(Constants.USERS_COLLECTION + "/" + userId);

        // Get a snapshot of the collection.
        collectionReference.get().addOnCompleteListener(task -> {

            UserModel userModel;

            if (task.isSuccessful()) {

                // Get the snapshot's documents.
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // Iterate over the documents.
                for (DocumentSnapshot document : documents) {

                    // Get the document's data.
                    Map<String, Object> documentData = document.getData();

                    // Do something with the document data.
                    userModel = new UserModel(userId, (String) documentData.get("emailAddress"), (String) documentData.get("photoUrl"), (String) documentData.get("userName"));

                    // Create a User object or use the retrieved information as needed
                    UserEntity userEntity = new UserEntity();
                    userEntity.email = userModel.email;
                    userEntity.userName = userModel.userName;
                    userEntity.uid = userModel.uid;
                    userEntity.photo = userModel.photo;

                    // Insert the user data into your local database or perform any other necessary actions
                    userDao.insertAll(userEntity);

                    userModelMutableLiveData.postValue(userModel);
                }

            } else {

                // Handle the error.
                userModelMutableLiveData.postValue(null);
            }
        });

        return userModelMutableLiveData;
    }



}
