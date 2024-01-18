package com.news.headline.repositories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.news.headline.models.PostModel;
import com.news.headline.utils.Constants;
import com.news.headline.views.PostsActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

public class PostsRepo {


    private static PostsRepo instance;
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();


    public static PostsRepo getInstance(Activity mContext) {
        if (instance == null) {
            instance = new PostsRepo();
        }

        return instance;
    }

    public MutableLiveData<List<PostModel>> fetchPosts() {

        MutableLiveData<List<PostModel>> posts = new MutableLiveData<>();
        List<PostModel> postsList = new ArrayList<>();

        // Create a FirebaseFireStore instance.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the collection.
        CollectionReference collectionReference = db.collection(Constants.POSTS_COLLECTION);

        // Get a snapshot of the collection.
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Get the snapshot's documents.
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // Iterate over the documents.
                for (DocumentSnapshot document : documents) {

                    // Get the document's data.
                    Map<String, Object> documentData = document.getData();

                    // Do something with the document data.
                    postsList.add(new PostModel((String) documentData.get(Constants.PHOTO_PATH), (String) documentData.get(Constants.DESCRIPTION)
                            , (String) documentData.get(Constants.TITLE), (String) documentData.get(Constants.USERNAME), (String) documentData.get(Constants.DATE)));
                }

                posts.postValue(postsList);

            } else {

                // Handle the error.
                posts.setValue(Collections.emptyList());
            }
        });

        return posts;
    }


    // Save post information to FireStore
    public MutableLiveData<String> createPost(String title, String description, Uri filePath, String userName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection(Constants.POSTS_COLLECTION);
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        if (filePath != null) {

            // Defining the child of storageReference
            StorageReference ref = storage.getReference().child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {

                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                Map<String, Object> post = new HashMap<>();
                                post.put(Constants.USERNAME, userName);
                                post.put(Constants.TITLE, title);
                                post.put(Constants.DESCRIPTION, description);
                                post.put(Constants.PHOTO_PATH, task.getResult().toString());
                                post.put(Constants.DATE, new SimpleDateFormat("MM/dd/yy", Locale.US).format(Calendar.getInstance().getTime()));

                                postsRef.add(post).addOnSuccessListener(documentReference -> mutableLiveData.postValue("success"))
                                        .addOnFailureListener(e -> mutableLiveData.postValue("error"));
                            } else {
                                mutableLiveData.postValue("errorImage");
                            }
                        });

                    }).addOnFailureListener(e -> mutableLiveData.postValue("errorImage"));
        }


        return mutableLiveData;
    }


}
