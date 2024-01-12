package com.news.headline.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.news.headline.dialogs.CustomProgressDialog;
import com.news.headline.utils.FirebaseImageUploader;
import com.news.headline.R;
import com.news.headline.db.AppDatabase;
import com.news.headline.db.dao.UserDao;
import com.news.headline.databinding.ActivityCreatePostBinding;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding bind;

    private static final int REQUEST_IMAGE_CAPTURE_PHOTO = 12;
    private static final int REQUEST_IMAGE_GALLERY = 15;
    private Context context;
    private Bitmap photo;
    private android.app.AlertDialog dialog;

    private FirebaseStorage storage;
    private AppDatabase db;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        context = this;
        storage = FirebaseStorage.getInstance();

        bind.addPhoto.setOnClickListener(view -> addPhoto());

        bind.publish.setOnClickListener(view -> createPost());

        bind.back.setOnClickListener(view -> finish());

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();

        userDao = db.userDao();

    }

    private void createPost() {
        initDialog(getString(R.string.creating_post));
        progressState(true);

        String title = bind.title.getText().toString();
        String description = bind.description.getText().toString();


        if (isValidPost(title, description)) {

            // Call FirebaseImageUploader.uploadImage
            FirebaseImageUploader.uploadImage(photo, generateImageName(bind.title.getText().toString()), new FirebaseImageUploader.OnImageUploadListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    // Image uploaded successfully, handle success
                    // You may save the download URL to preferences or elsewhere
                    // if you need to use it later
                    // ...
                    // Valid post, proceed with creating the post
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {

                        uploadImage(bind.title.getText().toString(), currentUser);

                    } else {
                        showToast("User not signed in. Unable to create a post.");
                        progressState(false);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle failure
                    // ...
                    showToast("Registration failed: " + errorMessage);
                    progressState(false);
                }
            });


        } else {
            // Invalid post, show an error message or handle accordingly
            showToast("Invalid post. Title and description are required.");
            progressState(false);
        }
    }

    // Validate post information
    private boolean isValidPost(String title, String description) {
        return title != null && !title.trim().isEmpty() &&
                description != null && !description.trim().isEmpty();
    }

    // Save post information to Firestore
    private void savePostToFirestore(String userName, String title, String description, String photoUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");


        Map<String, Object> post = new HashMap<>();
        post.put("userName", userName);
        post.put("title", title);
        post.put("description", description);
        post.put("photoUrl", photoUrl);
        post.put("date", new SimpleDateFormat("MM/dd/yy", Locale.US).format(Calendar.getInstance().getTime()));

        postsRef.add(post)
                .addOnSuccessListener(documentReference -> {
                    // Post created successfully
                    showToast("Post created successfully!");
                    startActivity(new Intent(this, PostsActivity.class));
                    progressState(false);
                })
                .addOnFailureListener(e -> {
                    // Post creation failed
                    showToast("Failed to create post: " + e.getMessage());
                    progressState(false);
                });
    }

    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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


    private void addPhoto() {
        bind.addPhoto.setVisibility(View.GONE);
        bind.progress.setVisibility(View.VISIBLE);
        showImageDialog();

    }


    private void showImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PHOTO);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {


            System.out.println("ImageState: " + 0);
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

            if (photo != null) {

                //upload to firebase and get link back
                bind.imageView.setImageBitmap(photo);

            } else {
                // Handle the case where the Bitmap is null
                Toast.makeText(context, "Failed to retrieve the image", Toast.LENGTH_SHORT).show();
            }

        }

        bind.progress.setVisibility(View.GONE);
    }


    // Method to generate image name based on email address
    private String generateImageName(String emailAddress) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Update the digest with the bytes of the email address
            byte[] emailBytes = emailAddress.getBytes();
            byte[] hashedBytes = digest.digest(emailBytes);

            // Convert the hashed bytes to a hexadecimal string
            StringBuilder builder = new StringBuilder();
            for (byte b : hashedBytes) {
                builder.append(String.format("%02x", b));
            }

            // Return the generated image name
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it or throw a custom exception)
            return null;
        }
    }


    private void uploadImage(String imageName, FirebaseUser currentUser) {

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        String finalName = imageName + ".jpg";

        // Create a reference to "mountains.jpg"
        StorageReference imageRef = storageRef.child(finalName);

        // Create a reference to 'images/mountains.jpg'
        StorageReference imagesRef = storageRef.child("images/" + finalName);

        // While the file names are the same, the references point to different files
        imageRef.getName().equals(imagesRef.getName());    // true
        imageRef.getPath().equals(imagesRef.getPath());    // false

        // Get the data from an ImageView as bytes
        bind.imageView.setDrawingCacheEnabled(true);
        bind.imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) bind.imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // User is signed in, create a post for the current user
                System.out.println("ExceptionUploadingPhotos: " + exception.getMessage());
                showToast(exception.getMessage());
                progressState(false);

            }
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                System.out.println("DownloadUrl: " + downloadUrl);



                // Now you can use the download URL as needed
                savePostToFirestore(userDao.getAll().get(0).userName, bind.title.getText().toString(), bind.description.getText().toString(), downloadUrl);
                // ...
            }).addOnFailureListener(exception -> {
                // Handle any errors that may occur while getting the download URL
                System.out.println("ExceptionUploadingPhotos: " + exception.getMessage());
                showToast(exception.getMessage());
                progressState(false);
            });
        });
    }

}








