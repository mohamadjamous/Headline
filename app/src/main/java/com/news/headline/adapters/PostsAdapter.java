package com.news.headline.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.news.headline.databinding.PostListItemBinding;
import com.news.headline.models.PostModel;
import com.news.headline.utils.Constants;
import com.news.headline.utils.Listeners;
import com.news.headline.views.PostActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private ArrayList<PostModel> modelObjectArrayList;
    private Context context;
    private Listeners listener;

    public PostsAdapter(ArrayList<PostModel> modelObjectArrayList, Context context, Listeners listener) {
        this.modelObjectArrayList = modelObjectArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(PostListItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, final int position) {

        PostModel postModel = modelObjectArrayList.get(position);

        System.out.println("postModel.photoUrl =  " + postModel.photoUrl);
        Glide.with(context)
                .load(postModel.photoUrl)
                .into(holder.binding.imageView);

        holder.binding.title.setText(truncateString(postModel.description));
        holder.binding.user.setText(postModel.userName);
        holder.binding.date.setText(postModel.date);

        holder.binding.getRoot().setOnClickListener(view ->
        {
            PostModel postModel1 = modelObjectArrayList.get(position);
            listener.onPostClick(postModel1);
        });

    }

    @Override
    public int getItemCount() {

        return modelObjectArrayList == null ? 0 :
                modelObjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PostListItemBinding binding;

        public ViewHolder(PostListItemBinding rowXmlViewBinding) {
            super(rowXmlViewBinding.getRoot());
            this.binding = rowXmlViewBinding;
        }
    }

    public static String truncateString(String string) {
        if (string.length() > 40) {
            return string.substring(0, 40) + "...";
        } else {
            return string;
        }
    }


}