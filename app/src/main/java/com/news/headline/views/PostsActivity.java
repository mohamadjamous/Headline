package com.news.headline.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.news.headline.adapters.PostsAdapter;
import com.news.headline.databinding.ActivityPostsBinding;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.R;
import com.news.headline.models.PostModel;
import com.news.headline.viewmodels.PostViewModel;
import com.news.headline.viewmodels.UserViewModel;

import java.util.ArrayList;


public class PostsActivity extends AppCompatActivity {

    public ActivityPostsBinding binding;
    private Context context;
    private AlertDialog dialog;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;
    private ArrayList<PostModel> postModels;


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

        fetchPosts();


        binding.createPost.setOnClickListener(view ->
        {
            //check if user exists
            if (!userDao.getAllUsers().isEmpty()) {
                startActivity(new Intent(this, CreatePostActivity.class));
            } else {
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


    private void fetchPosts()
    {
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
        binding.postsRecyclerView.setAdapter(new PostsAdapter( postModels ,getApplicationContext()));
    }


    private void firebaseAuth(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(authCredential)
                .addOnSuccessListener(authResult -> {


                    startActivity(new Intent(this, SignupActivity.class));


//                    User user = new User();
//                    user.uid = firebaseUser.getUid();
//                    user.email = firebaseUser.getEmail();
//                    userDao.insertAll();


                    binding.createPost.setEnabled(true);
                    progressState(false);

                }).addOnFailureListener(e -> {
                    System.out.println("ErrorState: " + 1);
                    Toast.makeText(context, getString(R.string.error_email_sign_in), Toast.LENGTH_LONG).show();
                    binding.createPost.setEnabled(true);
                    progressState(false);
                });
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);

        } catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            System.out.println("signInResult:failed code=" + e.getStatusCode());
            System.out.println("signInResult:failed code=" + e.getMessage());
            System.out.println("signInResult:failed code=" + e.getStatusMessage());
            System.out.println("signInResult:failed code=" + e.getLocalizedMessage());
            updateUI(null);
        }

    }

    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {

            System.out.println("AccountEmailValue: " + account.getEmail());
            System.out.println("Email: " + account.getEmail());
            System.out.println("FirstName: " + account.getGivenName());
            System.out.println("LastName: " + account.getFamilyName());

            Intent intent = new Intent(new Intent(this, SignupActivity.class));
            Bundle bundle = new Bundle();
            bundle.putSerializable("email", account.getEmail());
            bundle.putSerializable("name", account.getDisplayName() + " " + account.getFamilyName());
            intent.putExtras(bundle);
            startActivity(intent);

            binding.createPost.setEnabled(true);

        } else {

            System.out.println("ErrorState: " + 2);
            Toast.makeText(context, getString(R.string.error_email_sign_in), Toast.LENGTH_LONG).show();
            binding.createPost.setEnabled(true);
        }

        progressState(false);
    }


    private void initDialog(String message) {
        dialog = CustomProgressDialog.showCustomDialog(context, message, R.color.white);
    }




}