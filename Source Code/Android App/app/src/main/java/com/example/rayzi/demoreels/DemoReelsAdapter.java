package com.example.rayzi.demoreels;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.databinding.DemoReelsItemBinding;
import com.example.rayzi.modelclass.ReliteRoot;

import java.util.ArrayList;
import java.util.List;

public class DemoReelsAdapter extends RecyclerView.Adapter<DemoViewHolder> {
    private static final String TAG = "DemoReelsAdapter";
    private List<ReliteRoot.VideoItem> reels = new ArrayList<>();
    private Context context;
    private boolean stopped = false;
    private int stopPosition;

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DemoReelsItemBinding binding = DemoReelsItemBinding.inflate(inflater, parent, false);
        return new DemoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoViewHolder holder, int position) {
        holder.recyclerViewHorizontal.reset();
        holder.recyclerViewHorizontal.setId(View.generateViewId());
        Log.d(TAG, "onBindViewHolder: dataList[position].video === " + reels.get(position).getVideo());
        holder.recyclerViewHorizontal.setVideoUri(Uri.parse(reels.get(position).getVideo()));
        Glide.with(context).load(reels.get(position).getVideo()).centerCrop()
                .into(holder.binding.backBlurImage);
    }

    @Override
    public int getItemCount() {
        return reels.size();
    }

    public void addData(List<ReliteRoot.VideoItem> reels) {
        this.reels.addAll(reels);
        notifyItemRangeInserted(this.reels.size(), reels.size());
    }

    public void clear() {
        reels.clear();
        notifyDataSetChanged();
    }

}
