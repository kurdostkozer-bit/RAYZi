package com.example.rayzi.liveStreamming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemViewBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveViewUserAdapter extends RecyclerView.Adapter<LiveViewUserAdapter.ChatUserViewHolder> {

    private static final String TAG = "LiveViewUserAdapter";
    private JSONArray users = new JSONArray();
    Context context;

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter;

    public OnLiveUserAdapterClickLisnter getOnLiveUserAdapterClickLisnter() {
        return onLiveUserAdapterClickLisnter;
    }

    public void setOnLiveUserAdapterClickLisnter(OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter) {
        this.onLiveUserAdapterClickLisnter = onLiveUserAdapterClickLisnter;
    }

    @Override
    public void onBindViewHolder(ChatUserViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return users.length();
    }

    public void addData(JSONArray jsonArray) {

        users = jsonArray;
        notifyDataSetChanged();
    }

    public JSONArray getList() {
        return users;
    }

    public interface OnLiveUserAdapterClickLisnter {
        void onUserClick(JSONObject userDummy);
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemViewBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemViewBinding.bind(itemView);
        }

        public void setData(int position) {
            try {
                JSONObject userDummy = users.getJSONObject(position);
                Log.d(TAG, "setData: userDummy ==== " + userDummy.toString());
                Log.d("TAG", "setData: >>>>>>>>>>>>>>>>>>>>>>>>>> " + userDummy.getString("name"));

                if (userDummy.getBoolean("isAdd")) {
                        binding.imgview.setUserImage(userDummy.getString("image"), userDummy.getString("avatarFrameImage"), 10);
                }

                binding.getRoot().setOnClickListener(v -> {
                    Log.e("TAG", "setData: " + userDummy);
                    onLiveUserAdapterClickLisnter.onUserClick(userDummy);
                });

            } catch (Exception o) {
                Log.e("TAG", "setData:viewadapter " + o.getMessage());
            }
        }
    }
}
