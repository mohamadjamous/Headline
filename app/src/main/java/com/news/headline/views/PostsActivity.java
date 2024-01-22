package com.news.headline.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.news.headline.R;
import com.news.headline.adapters.PostsAdapter;
import com.news.headline.databinding.ActivityPostsBinding;
import com.news.headline.db.entities.UserEntity;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.models.PostModel;
import com.news.headline.utils.Constants;
import com.news.headline.utils.Listeners;
import com.news.headline.viewmodels.PostViewModel;
import com.news.headline.viewmodels.UserViewModel;

import java.io.Serializable;
import java.util.ArrayList;


public class PostsActivity extends AppCompatActivity implements Listeners {

    public ActivityPostsBinding binding;
    private Context context;
    private AlertDialog dialog;
    private PostViewModel postViewModel;
    private ArrayList<PostModel> postModels;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        postViewModel.init(this);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init(this);

        binding.postsRecyclerView.setHasFixedSize(true);
        binding.postsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        binding.logout.setOnClickListener(view -> {
            userViewModel.logoutUser(userViewModel.fetchUserFromDatabase().getValue());
            restartApp(context);
        });

        binding.fab.setOnClickListener(view ->
        {
            UserEntity userEntity = userViewModel.fetchUserFromDatabase().getValue();
            //check if user exists
            if (userEntity != null) {
                System.out.println("UserId: " + userViewModel.fetchUserFromDatabase().getValue().uid);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.USERNAME, userEntity.userName);
                Intent intent = new Intent(PostsActivity.this, CreatePostActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }

        });

    }

    //show & hide progress bar4
    private void progressState(boolean show) {
        if (show) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }


    private void fetchPosts() {
        initDialog(getString(R.string.loading));
        progressState(true);

        postModels = new ArrayList<>();
        postViewModel.requestToFetchPosts().observe(this, postModels -> {

            if (postModels != null) {
                PostsActivity.this.postModels.addAll(postModels);
                setPostsAdapter();
            } else {
                Toast.makeText(this, "No Posts", Toast.LENGTH_SHORT).show();
            }
            progressState(false);
        });
    }

    private void setPostsAdapter() {

        System.out.println("PostsModelsSize: " + postModels.size());
        binding.postsRecyclerView.setAdapter(new PostsAdapter(postModels, getApplicationContext(), this));
    }


    private void initDialog(String message) {
        dialog = CustomProgressDialog.showCustomDialog(context, message, R.color.white);
    }


    @Override
    protected void onStart() {
        super.onStart();

        hideShowLogout();
        fetchPosts();
    }


    private void hideShowLogout() {
        UserEntity userEntity = userViewModel.fetchUserFromDatabase().getValue();

        //check if user exists
        if (userEntity != null)
            binding.logout.setVisibility(View.VISIBLE);
        else
            binding.logout.setVisibility(View.GONE);
    }

    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    @Override
    public void onPostClick(PostModel postModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.POST, postModel);
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}