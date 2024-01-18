package com.news.headline.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.models.PostModel;
import com.news.headline.adapters.PostsAdapter;
import com.news.headline.R;
import com.news.headline.databinding.ActivityBookMarkBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class BookMarkActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private ActivityBookMarkBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityBookMarkBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());




    }


    private void fetchBookmarks() {

        initDialog(getString(R.string.fetching_posts));
        progressState(true);

        // Get the current user's UID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "bookmarks" collection
        db.collection("bookmarks")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        ArrayList<PostModel> posts = new ArrayList<>();
                        // Handle successful query
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the 'posts' object from the document
                            // Replace "posts" with the actual field name in your document
                            Map<String, Object> postMap = document.getData();

                            String description = (String) postMap.get("description");
                            String title = (String) postMap.get("title");
                            String photoURL = (String) postMap.get("photoURL");
                            String userName = (String) postMap.get("userName");
                            String date = (String) postMap.get("date");
                            posts.add(new PostModel(photoURL, description, title, userName, date));
                        }

                        bind.recyclerView.setLayoutManager(new LinearLayoutManager(this));

//                        bind.recyclerView.setAdapter(new PostsAdapter(posts));

                    } else {
                        // Handle failed query
                        showToast(this,"FetchBookmarks "+  " Error getting documents: "+ task.getException());
                    }

                    progressState(false);
                });
    }

    // Helper method to show a toast message
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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