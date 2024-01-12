package com.news.headline.repositories;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.news.headline.models.PostModel;
import com.news.headline.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostsRepo {


    private static PostsRepo instance;


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
                    postsList.add( new PostModel((String) documentData.get("photoUrl"), (String) documentData.get("description")
                            , (String) documentData.get("title"), (String) documentData.get("userName"), (Date) documentData.get("date")));
                }

                posts.postValue(postsList);

            } else {

                // Handle the error.
                posts.setValue(Collections.emptyList());
            }
        });

        return posts;
    }


}
