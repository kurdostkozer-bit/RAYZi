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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BannedListAdapter extends RecyclerView.Adapter<BannedListAdapter.Myviewholder> {

    JSONArray blockedlist = new JSONArray();
    Context context;
    onUnblockListener onUnblockListener;

    public BannedListAdapter(Context context, onUnblockListener onUnblockListener) {
        this.context = context;
        this.onUnblockListener = onUnblockListener;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new Myviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bannedlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder holder, int position) {
        try {
            holder.setData(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return blockedlist.length();
    }

    public void addData(JSONArray newBlockedlist) {

        blockedlist = newBlockedlist;
        notifyDataSetChanged();
    }



    public class Myviewholder extends RecyclerView.ViewHolder{
        ItemBannedlistBinding binding;
        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBannedlistBinding.bind(itemView);
        }

        public void setData(int position) throws JSONException {

            JSONObject blockedUserList = blockedlist.getJSONObject(position);
            JSONObject jsonObject1 = blockedUserList.getJSONObject("blockedUserId");
            String username = jsonObject1.getString("name");
            String image = jsonObject1.getString("image");

            binding.tvUsername.setText(username);
            Glide.with(context).load(image).into(binding.ivUserProfile);

            binding.tvUnblock.setOnClickListener(v -> {
                try {
                    onUnblockListener.onUnblock(jsonObject1.getString("_id"),position);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

        }


    }

    public interface onUnblockListener{

        void onUnblock(String id, int position);

    }

}
