package com.news.headline.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.news.headline.R;
import com.news.headline.databinding.ActivityPostBinding;
import com.news.headline.models.PostModel;
import com.news.headline.utils.Constants;

public class PostActivity extends AppCompatActivity {


    private ActivityPostBinding binding;
    private PostModel postModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postModel = (PostModel) getIntent().getExtras().getSerializable(Constants.POST);
        if (getIntent().getExtras() != null && postModel != null) {
            Glide.with(this)
                    .load(postModel.photoUrl)
                    .into(binding.imageView);

            binding.titleText.setText(postModel.title);
            binding.description.setText(postModel.description);
            binding.user.setText(postModel.userName);
            binding.date.setText(postModel.date);

        } else {
            Toast.makeText(this, getString(R.string.error_loading_post), Toast.LENGTH_SHORT).show();
        }


        binding.close.setOnClickListener(view -> finish());
        binding.copyText.setOnClickListener(view -> copyToClipboard(binding.description));

    }

    public void copyToClipboard(TextView textView) {
        android.content.ClipboardManager clipboardManager = (ClipboardManager) textView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, getString(R.string.copied_text), Toast.LENGTH_SHORT).show();
    }


}