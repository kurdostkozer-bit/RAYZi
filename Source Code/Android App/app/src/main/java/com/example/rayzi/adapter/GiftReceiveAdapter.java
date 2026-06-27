package com.example.rayzi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.databinding.ItemRecieveGiftBinding;
import com.example.rayzi.modelclass.GiftRoot;

import java.util.ArrayList;
import java.util.List;


public class GiftReceiveAdapter extends RecyclerView.Adapter<GiftReceiveAdapter.GiftViewHolder> {

    Context context;
    List<GiftRoot.GiftItem> giftList = new ArrayList<>();


    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new GiftViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recieve_gift, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        Animation animLtoR = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.anim_slide_left_to_right_gift);
        holder.binding.layoutGiftAni.startAnimation(animLtoR);

        holder.binding.name.setText(giftList.get(position).getName());
        String receiverNames = giftList.get(position).getReceiverUserName().toString()
                .replace("[", "")  // Remove opening bracket
                .replace("]", "");
        holder.binding.tvReceivername.setText("send a gift to "+ receiverNames);

        if (giftList.get(position).getImage().contains(".gif")) {
            Glide.with(context.getApplicationContext()).asGif().load(BuildConfig.BASE_URL +giftList.get(position).getImage()).circleCrop().into(holder.binding.gift);
        } else {
            Glide.with(context.getApplicationContext()).load(BuildConfig.BASE_URL + giftList.get(position).getImage()).circleCrop().into(holder.binding.gift);
        }
        Glide.with(holder.binding.imgGiftCount).load(RayziUtils.getImageFromNumber(giftList.get(position).getCount()))
                .into(holder.binding.imgGiftCount);

        new Handler().postDelayed(() -> {
            holder.binding.layoutGiftAni.setVisibility(View.GONE);
        }, 4000);

    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }

    public void remove(GiftRoot.GiftItem gift) {

    }

    public void addData(List<GiftRoot.GiftItem> gift) {
        giftList.addAll(gift);
        notifyItemRangeInserted(giftList.size(), gift.size());
    }


    public class GiftViewHolder extends RecyclerView.ViewHolder {
        ItemRecieveGiftBinding binding;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
