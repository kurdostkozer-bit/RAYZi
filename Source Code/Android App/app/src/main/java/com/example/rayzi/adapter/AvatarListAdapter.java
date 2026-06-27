package com.example.rayzi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemAvatarListBinding;
import com.example.rayzi.modelclass.SvgaListRoot;

import java.util.ArrayList;
import java.util.List;

public class AvatarListAdapter extends RecyclerView.Adapter<AvatarListAdapter.SvgaViewHolder> {
    private static final String TAG = "AvatarListAdapter";
    List<SvgaListRoot.DataItem> avatarList = new ArrayList<>();
    Context context;
    SessionManager sessionManager;
    onAvatarClickListener onAvatarClickListener;

    public void setOnAvatarClickListener(AvatarListAdapter.onAvatarClickListener onAvatarClickListener) {
        this.onAvatarClickListener = onAvatarClickListener;
    }

    @NonNull
    @Override
    public SvgaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        return new SvgaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_avatar_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SvgaViewHolder holder, int position) {
        holder.setData(avatarList.get(position));
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SvgaViewHolder extends RecyclerView.ViewHolder {
        ItemAvatarListBinding binding;

        public SvgaViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAvatarListBinding.bind(itemView);
        }

        public void setData(SvgaListRoot.DataItem svgaItem) {
            binding.svgaName.setText(svgaItem.getName());
            binding.priceDiamonds.setText(String.valueOf(svgaItem.getDiamond()));
            binding.validationTag.setText(" /" + svgaItem.getValidationTag());

            Glide.with(context).load(sessionManager.getUser().getImage()).circleCrop().into(binding.userImage);
            Glide.with(context).load(BuildConfig.BASE_URL + svgaItem.getImage()).into(binding.avatarFrame);

            if (svgaItem.isIsPurchase())
                binding.btnpurchase.setImageResource(R.drawable.select_btn);
            if (svgaItem.isIsSelected())
                binding.btnpurchase.setImageResource(R.drawable.selected_btn);

            // click
            binding.btnpurchase.setOnClickListener(v -> {
                Log.d(TAG, "setData: svgaItem.isIsPurchase() =====m " + svgaItem.isIsPurchase());
                Log.d(TAG, "setData: svgaItem.isIsSelected() ====   " + svgaItem.isIsSelected());
                if (svgaItem.isIsPurchase() && !svgaItem.isIsSelected()) {
                    for (int i = 0; i < avatarList.size(); i++) {
                        if (avatarList.get(i).isIsSelected()) {
                            avatarList.get(i).setSelected(false);
                        }
                    }
                }
                onAvatarClickListener.onAvatarClick(svgaItem, binding);
            });
        }
    }

    public interface onAvatarClickListener {
        void onAvatarClick(SvgaListRoot.DataItem svgaItem, ItemAvatarListBinding binding);
    }

    public void addData(List<SvgaListRoot.DataItem> svgaItemList) {
        this.avatarList.addAll(svgaItemList);
        notifyItemRangeInserted(this.avatarList.size(), svgaItemList.size());
    }

    public void clear() {
        avatarList.clear();
        notifyDataSetChanged();
    }
}
