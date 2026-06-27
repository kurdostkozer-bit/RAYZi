package com.example.rayzi.audioLive;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemAudioUsersBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveViewUserAdapter extends RecyclerView.Adapter<LiveViewUserAdapter.ChatUserViewHolder> {

    private static final String TAG = "LiveViewUserAdapter";
    Context context;
    OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter;
    private JSONArray users = new JSONArray();

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_users, parent, false));
    }

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
     /*   for (int i = 0; i < jsonArray.length(); i++) {
            try {
            JSONObject object=jsonArray.getJSONObject(i);

              *//*  for (int j = 0; j < this.users.length(); j++) {
                    JSONObject user=users.getJSONObject(j);
                    if (user.get("userId").equals(object.get("userId"))){
                        users.remove(j);
                    }
                }*//*
                this.users.put(jsonArray.get(i));
                notifyItemInserted(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    public JSONArray getList() {
        return users;
    }

    public interface OnLiveUserAdapterClickLisnter {
        void onUserClick(JSONObject userDummy);
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemAudioUsersBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemAudioUsersBinding.bind(itemView);
        }

        public void setData(int position) {
            try {
                JSONObject userDummy = users.getJSONObject(position);
                Log.d(TAG, "setData: userDummy.toString() == " + userDummy.toString());
                String avatarFrame = "";
                if (userDummy.has("avatarFrameImage")) {
                    if (!userDummy.getString("avatarFrameImage").isEmpty()) {
                        avatarFrame = userDummy.getString("avatarFrameImage");
                    }
                }
                binding.imgUser1.setUserImage(userDummy.getString("image"), avatarFrame, 13);
                binding.userName.setText(userDummy.getString("name"));
                binding.gender.setText(userDummy.getString("gender"));
                binding.location.setText(userDummy.getString("country"));

                binding.getRoot().setOnClickListener(v -> onLiveUserAdapterClickLisnter.onUserClick(userDummy));

            } catch (Exception o) {
                Log.e("TAG", "setData:viewadapter " + o.getMessage());
            }
        }
    }
}
