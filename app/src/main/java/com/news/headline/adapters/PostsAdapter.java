package com.news.headline.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.news.headline.models.PostModel;
import com.news.headline.databinding.PostListItemBinding;

import java.util.ArrayList;
import java.util.Locale;

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

        PostModel modelObject = modelObjectArrayList.get(position);
        holder.bind.title.setText(modelObject.title);
        holder.bind.user.setText(modelObject.userName);
        holder.bind.date.setText(new SimpleDateFormat("MM/dd/yy", Locale.US).format(modelObject.date));

    }

    @Override
    public int getItemCount() {

        return modelObjectArrayList == null ? 0 :
                modelObjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PostListItemBinding bind;

        public ViewHolder(PostListItemBinding rowXmlViewBinding) {
            super(rowXmlViewBinding.getRoot());
            this.bind = rowXmlViewBinding;
        }
    }
}