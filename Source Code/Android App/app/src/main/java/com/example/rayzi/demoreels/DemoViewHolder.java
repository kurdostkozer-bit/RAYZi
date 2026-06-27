package com.example.rayzi.demoreels;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.databinding.DemoReelsItemBinding;

public class DemoViewHolder extends RecyclerView.ViewHolder {
    public InstaLikePlayerView recyclerViewHorizontal;
    public DemoReelsItemBinding binding;

    public DemoViewHolder(View root) {
        super(root);
        recyclerViewHorizontal = null;  // Initialize later, if needed
    }

    public DemoViewHolder(DemoReelsItemBinding binding) {
        this(binding.getRoot());
        this.binding = binding;
        recyclerViewHorizontal = binding.feedPlayerView;
    }
}