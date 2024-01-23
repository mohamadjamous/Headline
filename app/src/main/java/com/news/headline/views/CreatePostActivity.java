package com.news.headline.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.news.headline.R;
import com.news.headline.databinding.ActivityCreatePostBinding;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.utils.Constants;
import com.news.headline.viewmodels.PostViewModel;
import com.news.headline.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;

    private static final int REQUEST_IMAGE_CAPTURE_PHOTO = 12;
    private static final int REQUEST_IMAGE_GALLERY = 15;
    private Context context;
    private Bitmap photo;
    private android.app.AlertDialog dialog;
    private PostViewModel viewModel;
    private UserViewModel userViewModel;
    private Uri filePath;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        viewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        viewModel.init(this);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.init(this);


        userName = getIntent().getStringExtra(Constants.USERNAME);


        binding.back.setOnClickListener(view -> finish());
        binding.addPhoto.setOnClickListener(view -> addPhoto());
        binding.publish.setOnClickListener(view -> createPost());
        binding.editPhoto.setOnClickListener(view -> addPhoto());


    }

    private void createPost() {

        String title = binding.title.getText().toString();
        String description = binding.description.getText().toString();

        // Check if the title is empty.
        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the description is empty.
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the title contains at least one word.
        String[] titleWords = title.split(" ");
        if (titleWords.length < 1) {
            Toast.makeText(this, "Title must contain at least one word.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the description contains at least one sentence.
        String[] descriptionSentences = description.split(".");
        if (descriptionSentences.length < 1) {
            Toast.makeText(this, "Description must contain at least one sentence.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filePath == null) {
            Toast.makeText(context, "Add post photo!", Toast.LENGTH_SHORT).show();
            return;
        }


        initDialog(getString(R.string.creating_post));
        progressState(true);

        viewModel.createPost(binding.title.getText().toString(), binding.description.getText().toString(),
                filePath, userName).observe(this, message -> {

            if (message.equals("success")) {
                Toast.makeText(context, "Post is live", Toast.LENGTH_SHORT).show();
                finish();
            } else if (message.equals("errorImage")) {
                Toast.makeText(context, "Error uploading photo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error creating post", Toast.LENGTH_SHORT).show();
            }

            progressState(false);
        });

    }


    private void initDialog(String message) {
        dialog = CustomProgressDialog.showCustomDialog(context, message, R.color.white);
        dialog.setCancelable(false);
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


    private void addPhoto() {
        binding.addPhoto.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
        showImageDialog();

    }


    private void showImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PHOTO);
                }
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
                binding.addPhoto.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_PHOTO && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            if (photo != null) {
                filePath = getImageUri(this, photo);
                binding.imageView.setImageBitmap(photo);
                binding.editPhoto.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(context, "Error choosing image", Toast.LENGTH_SHORT).show();
            }




        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                binding.imageView.setImageBitmap(bitmap);
                binding.editPhoto.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                // Log the exception
                System.out.println("ErrorMessage: " + e.getMessage());
                Toast.makeText(context, "Error choosing image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else
        {
            binding.addPhoto.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
        }


        System.out.println("FileUri: " + filePath);


        binding.progress.setVisibility(View.GONE);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}