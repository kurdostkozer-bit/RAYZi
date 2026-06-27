package com.example.rayzi.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemChatImageBinding;
import com.example.rayzi.databinding.ItemChatStikerBinding;
import com.example.rayzi.databinding.ItemChatTextBinding;
import com.example.rayzi.modelclass.ChatItem;
import com.example.rayzi.socket.MySocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_TYPE = 3;
    private static final int PHOTO_TYPE = 1;
    private static final int EMOJI_TYPE = 2;
    private Context context;
    String localUserId = "";
    private List<ChatItem> chatDummyList = new ArrayList<>();
    private String guestUserImage;
    private String localUserImage;
    private String guestUserAvatarImage;
    private String localUserAvatarImage;

    OnChatItemClickLister onChatItemClickLister;

    public OnChatItemClickLister getOnChatItemClickLister() {
        return onChatItemClickLister;
    }

    public void setOnChatItemClickLister(OnChatItemClickLister onChatItemClickLister) {
        this.onChatItemClickLister = onChatItemClickLister;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("TAG", "getItemViewType: " + chatDummyList.get(position).getMessageType());


        return chatDummyList.get(position).getMessageType().equalsIgnoreCase("message") ? TEXT_TYPE : PHOTO_TYPE;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TEXT_TYPE) {
            return new ChatTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text, parent, false));
        } else if (viewType == PHOTO_TYPE) {
            return new ChatImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_image, parent, false));
        } else {
            return new ChatStikerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_stiker, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatItem chatItem = chatDummyList.get(position);
        if (getItemViewType(position) == TEXT_TYPE) {
            ((ChatTextViewHolder) holder).setData(position);
        } else if (getItemViewType(position) == PHOTO_TYPE) {
            ((ChatImageViewHolder) holder).setData(position);
        } else {
            ((ChatStikerViewHolder) holder).setData(position);
        }

        emitMessageReadStatus(chatItem.getId());
    }

    @Override
    public int getItemCount() {
        return chatDummyList.size();
    }

    public void addData(List<ChatItem> chatDummyList) {
        this.chatDummyList.addAll(chatDummyList);
        notifyItemRangeInserted(this.chatDummyList.size(), chatDummyList.size());
    }

    public void initGuestUser(String guestUserDummy, String guestAvatarImage) {
        Log.d("TAG", "initGuestUserImage: " + guestUserDummy);
        this.guestUserImage = guestUserDummy;
        this.guestUserAvatarImage = guestAvatarImage;
    }

    public void initLocalUser(String userDummy, String userAvatarImage) {
        this.localUserAvatarImage = userAvatarImage;
        this.localUserImage = userDummy;
    }

    public void initLocalUserId(String localUserId) {
        this.localUserId = localUserId;
    }

    public void addSingleChat(ChatItem chatUserItem) {
        chatDummyList.add(0, chatUserItem);
        notifyItemInserted(0);
    }

    public void removeSingleItem(int position) {
        chatDummyList.remove(chatDummyList.get(position));
        notifyDataSetChanged();
    }

    public void clear() {
        chatDummyList.clear();
        notifyDataSetChanged();
    }

    public interface OnChatItemClickLister {
        void onLongPress(ChatItem chatDummy, int position);
    }

    public class ChatTextViewHolder extends RecyclerView.ViewHolder {
        ItemChatTextBinding binding;

        public ChatTextViewHolder(View itemView) {
            super(itemView);
            binding = ItemChatTextBinding.bind(itemView);
        }

        public void setData(int position) {
            ChatItem chatDummy = chatDummyList.get(position);
            binding.imgUser1.setUserImage(guestUserImage, guestUserAvatarImage, 10);
            binding.imgUser2.setUserImage(localUserImage, localUserAvatarImage, 10);

            if (chatDummy.getSenderId().equals(localUserId)) {  // match with local mens sender is local user
                binding.imgUser1.setVisibility(View.INVISIBLE);
                binding.imgUser2.setVisibility(View.VISIBLE);
                binding.space2.setVisibility(View.GONE);
                binding.space1.setVisibility(View.VISIBLE);
                binding.tvText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_right));
                binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.white));
                binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.chatText));
            } else {
                binding.imgUser2.setVisibility(View.INVISIBLE);
                binding.imgUser1.setVisibility(View.VISIBLE);
                binding.space1.setVisibility(View.GONE);
                binding.space2.setVisibility(View.VISIBLE);
                binding.tvText.setTextColor(ContextCompat.getColor(context, R.color.white));
                binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
                binding.tvText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_left));
            }
            binding.tvText.setText(chatDummy.getMessage());
            binding.getRoot().setOnLongClickListener(v -> {
                onChatItemClickLister.onLongPress(chatDummy, position);
                return true;
            });
        }
    }

    public class ChatStikerViewHolder extends RecyclerView.ViewHolder {
        ItemChatStikerBinding binding;

        public ChatStikerViewHolder(View itemView) {
            super(itemView);
            binding = ItemChatStikerBinding.bind(itemView);
        }

        public void setData(int position) {
            ChatItem chatDummy = chatDummyList.get(position);
            binding.imgUser1.setUserImage(guestUserImage, guestUserAvatarImage, 10);
            binding.imgUser2.setUserImage(localUserImage, localUserAvatarImage, 10);
            Glide.with(itemView).load(chatDummy.getImage())
                    .apply(MainApplication.requestOptions)
                    .into(binding.tvImage);


            if (chatDummy.getSenderId().equals(localUserId)) {
                binding.imgUser1.setVisibility(View.INVISIBLE);
                binding.imgUser2.setVisibility(View.VISIBLE);
                binding.space2.setVisibility(View.GONE);
                binding.space1.setVisibility(View.VISIBLE);
                binding.tvImage.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_right));
                binding.tvImage.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));

            } else {
                binding.imgUser2.setVisibility(View.INVISIBLE);
                binding.imgUser1.setVisibility(View.VISIBLE);
                binding.space1.setVisibility(View.GONE);
                binding.space2.setVisibility(View.VISIBLE);
                binding.tvImage.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_chat_left));
            }
        }
    }

    private void emitMessageReadStatus(String messageId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", messageId);
            MySocketManager.getInstance().getSocket().emit("messageReadStatus", jsonObject);
            Log.d("ChatAdapter", "Emitted messageReadStatus for: " + messageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ChatImageViewHolder extends RecyclerView.ViewHolder {
        ItemChatImageBinding binding;

        public ChatImageViewHolder(View itemView) {
            super(itemView);
            binding = ItemChatImageBinding.bind(itemView);
        }

        public void setData(int position) {
            ChatItem chatDummy = chatDummyList.get(position);

            binding.imgUser1.setUserImage(guestUserImage, guestUserAvatarImage, 10);
            binding.imgUser2.setUserImage(localUserImage, localUserAvatarImage, 10);
            Glide.with(itemView).load(BuildConfig.BASE_URL + chatDummy.getImage())
                    .placeholder(R.drawable.placeholder_live)
                    .into(binding.mainImage);
            binding.mainImage.setAdjustViewBounds(true);


            if (chatDummy.getSenderId().equals(localUserId)) {  // match with local mens sender is local user
                binding.imgUser1.setVisibility(View.INVISIBLE);
                binding.imgUser2.setVisibility(View.VISIBLE);
                binding.space2.setVisibility(View.GONE);
                binding.space1.setVisibility(View.VISIBLE);
                binding.lytMain.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
            } else {
                binding.imgUser2.setVisibility(View.INVISIBLE);
                binding.imgUser1.setVisibility(View.VISIBLE);
                binding.space1.setVisibility(View.GONE);
                binding.space2.setVisibility(View.VISIBLE);
                binding.lytMain.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
            }

            binding.getRoot().setOnLongClickListener(v -> {
                onChatItemClickLister.onLongPress(chatDummy, position);
                return true;
            });
        }
    }
}
