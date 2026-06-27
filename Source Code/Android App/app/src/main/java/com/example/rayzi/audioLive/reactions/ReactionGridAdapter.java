package com.example.rayzi.audioLive.reactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemEmojiGridBinding;
import com.example.rayzi.modelclass.ReactionRoot;

import java.util.ArrayList;
import java.util.List;

public class ReactionGridAdapter extends RecyclerView.Adapter<ReactionGridAdapter.EmojiViewHolder> {

    OnReactionClickListner onReactionClickListner;
    List<ReactionRoot.DataItem> reactions = new ArrayList<>();
    private Context context;

    public OnReactionClickListner onReactionClickListner() {
        return onReactionClickListner;
    }

    public void setonReactionClickListner(OnReactionClickListner onEmojiSelectLister) {
        this.onReactionClickListner = onEmojiSelectLister;
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new EmojiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {

        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }

    public void addData(List<ReactionRoot.DataItem> giftRootDummy) {
        this.reactions.addAll(giftRootDummy);
        notifyItemRangeInserted(this.reactions.size(), giftRootDummy.size());
    }

    public void clear() {
        this.reactions.clear();
        notifyDataSetChanged();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        ItemEmojiGridBinding binding;

        public EmojiViewHolder(View itemView) {
            super(itemView);
            binding = ItemEmojiGridBinding.bind(itemView);
        }

        public void setData(int position) {
            ReactionRoot.DataItem gift = reactions.get(position);
            binding.layCoin.setVisibility(View.GONE);
            Glide.with(binding.getRoot()).load(gift.getImage())
                    .apply(MainApplication.requestOptions)
                    .thumbnail(Glide.with(context).load(R.drawable.loadergif))
                    .into(binding.imgEmoji);

            binding.tvGiftName.setText(gift.getName());

            binding.getRoot().setOnClickListener(v -> {
                //  binding.itememoji.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_selected_5dp));
                onReactionClickListner.onReactionClick(reactions.get(position));
            });
        }
    }
}
