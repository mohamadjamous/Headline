package com.news.headline.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class FirebaseImageUploader {

    private static final String TAG = "FirebaseImageUploader";

    public static void uploadImage(Bitmap bitmap, String imageName, OnImageUploadListener listener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file you want to upload
        StorageReference imageRef = storageRef.child("images/" + imageName);

        // Convert the bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the byte array to Firebase Storage
        imageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Handle the download URL
                        String downloadUrl = uri.toString();
                        listener.onSuccess(downloadUrl);
                    }).addOnFailureListener(exception -> {
                        // Handle errors during URL retrieval
                        Log.e(TAG, "Error getting download URL: " + exception.getMessage());
                        listener.onFailure(exception.getMessage());
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.e(TAG, "Error uploading image: " + exception.getMessage());
                    listener.onFailure(exception.getMessage());
                });
    }

    public interface OnImageUploadListener {
        void onSuccess(String downloadUrl);
        void onFailure(String errorMessage);
    }
}
