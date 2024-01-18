package com.news.headline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.news.headline.models.PostModel;
import com.news.headline.databinding.PostListItemBinding;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private ArrayList<PostModel> modelObjectArrayList;
    private Context context;

    public PostsAdapter(ArrayList<PostModel> modelObjectArrayList, Context context) {
        this.modelObjectArrayList = modelObjectArrayList;
        this.context = context;
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
        holder.binding.title.setText(postModel.title);
        holder.binding.user.setText(postModel.userName);
        holder.binding.date.setText(postModel.date);

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
}