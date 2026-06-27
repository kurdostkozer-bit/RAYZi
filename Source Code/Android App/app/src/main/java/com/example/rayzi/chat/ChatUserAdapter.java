package com.example.rayzi.chat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemChatusersBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    private Context context;
    OnClickListener onClickListener;
    private List<ChatUserListRoot.ChatUserItem> chatUserDummies = new ArrayList<>();

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatusers, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatUserAdapter.ChatUserViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return chatUserDummies.size();
    }

    public void addData(List<ChatUserListRoot.ChatUserItem> chatUserDummies) {
        this.chatUserDummies.addAll(chatUserDummies);
        notifyItemRangeInserted(this.chatUserDummies.size(), chatUserDummies.size());
    }

    public void clear() {
        chatUserDummies.clear();
        notifyDataSetChanged();
    }

    public interface OnClickListener {

        void onClick(int position, ChatUserListRoot.ChatUserItem chatUserDummy);

    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemChatusersBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemChatusersBinding.bind(itemView);
        }

        public void setData(int position) {
            ChatUserListRoot.ChatUserItem chatUserDummy = chatUserDummies.get(position);
            binding.imguser.setUserImage(chatUserDummy.getImage(), chatUserDummy.getAvatarFrameImage(), 10);
            binding.tvusername.setText(chatUserDummy.getName());

            binding.tvlastchet.setText(chatUserDummy.getMessage().trim());
            binding.tvtime.setText(chatUserDummy.getTime());
            binding.tvcountry.setText(chatUserDummy.getCountry());
            if (chatUserDummy.getUnreadCount() == 0){
                binding.layCount.setVisibility(GONE);
            }else {
                binding.layCount.setVisibility(VISIBLE);
            }
            binding.tvCount.setText(String.valueOf(chatUserDummy.getUnreadCount()));
            binding.getRoot().setOnClickListener(v -> {
                onClickListener.onClick(position, chatUserDummy);
            });
        }
    }

}
