package com.example.rayzi.FakeChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rayzi.FakeChat.fakemodelclass.ChatRootFake;
import com.example.rayzi.R;
import com.example.rayzi.databinding.FakeItemChatBinding;
import com.example.rayzi.modelclass.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class FakeChatAdapter extends RecyclerView.Adapter<FakeChatAdapter.ChatTextViewHolder> {
    Context context;
    List<ChatRootFake> list = new ArrayList<>();
    OnClickListener onClickListener;

    @Override
    public ChatTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fake_item_chat, parent, false));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(ChatTextViewHolder holder, int position) {
        ChatRootFake chatDummy = list.get(position);
        holder.binding.imgUser1.setUserImage(chatDummy.getImage(),"", 10);
        holder.binding.imgUser2.setUserImage(chatDummy.getImage(), "", 10);

        if (chatDummy.getFlag() == 1) {
            holder.binding.imgUser1.setVisibility(View.INVISIBLE);
            holder.binding.imgUser2.setVisibility(View.VISIBLE);
            holder.binding.space2.setVisibility(View.GONE);
            holder.binding.space1.setVisibility(View.VISIBLE);
            holder.binding.tvText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_right));
            holder.binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.chatText));
        } else {
            holder.binding.imgUser2.setVisibility(View.INVISIBLE);
            holder.binding.imgUser1.setVisibility(View.VISIBLE);
            holder.binding.space1.setVisibility(View.GONE);
            holder.binding.space2.setVisibility(View.VISIBLE);
            holder.binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
            holder.binding.tvText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_left));
        }
        holder.binding.tvText.setText(chatDummy.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addSingleMessage(ChatRootFake chatRootFake) {
        list.add(0,chatRootFake);
        notifyItemInserted(0);
    }

    public void updateChatData(List<ChatRootFake> newChatData) {
        list.clear(); // Clear existing data
        list.addAll(newChatData); // Add new fake chat data
        notifyDataSetChanged(); // Notify adapter about data changes
    }

    public class ChatTextViewHolder extends RecyclerView.ViewHolder {
        FakeItemChatBinding binding;

        public ChatTextViewHolder(View itemView) {
            super(itemView);
            binding = FakeItemChatBinding.bind(itemView);
        }

        public void setData(int position) {

        }
    }

    public interface OnClickListener {

        void onImageClick(int position, String imageUrl);

    }

}
