package com.example.rayzi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemBannedlistBinding;
import com.example.rayzi.modelclass.BlockedUserListRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class BlockedUserListAdapter extends RecyclerView.Adapter<BlockedUserListAdapter.MyViewhoolder> {

    List<BlockedUserListRoot.BlockedUsersItem> blockedList = new ArrayList<>();
    Context context;
   onUnblockListener onUnblockListener;

    public BlockedUserListAdapter(Context context, BlockedUserListAdapter.onUnblockListener onUnblockListener) {
        this.context = context;
        this.onUnblockListener = onUnblockListener;
    }

    @NonNull
    @Override
    public BlockedUserListAdapter.MyViewhoolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bannedlist, parent, false);
        context = parent.getContext();
        return new BlockedUserListAdapter.MyViewhoolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUserListAdapter.MyViewhoolder holder, int position) {

        BlockedUserListRoot.BlockedUsersItem blockedUsersItem = blockedList.get(position);

        if (blockedUsersItem.getToUserId() != null ) {
            Glide.with(context).load(blockedUsersItem.getToUserId().getImage()).into(holder.binding.ivUserProfile);
            holder.binding.tvUsername.setText(blockedUsersItem.getToUserId().getName());
            holder.binding.tvUnblock.setOnClickListener(v -> {
                onUnblockListener.onUnblock(blockedUsersItem.getToUserId().getId(), position);
            });
        }

    }

    @Override
    public int getItemCount() {
        return blockedList.size();

    }

    public void addData(List<BlockedUserListRoot.BlockedUsersItem> blockeduserList) {

        blockedList = blockeduserList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < blockedList.size()) {
            blockedList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, blockedList.size());
        }
    }

    public interface onUnblockListener{
        void onUnblock(String id, int position);
    }

    public class MyViewhoolder extends RecyclerView.ViewHolder {

        ItemBannedlistBinding binding;
        public MyViewhoolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBannedlistBinding.bind(itemView);
        }
    }
}
