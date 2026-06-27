package com.example.rayzi.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemGiftUserlistBinding;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    Context context;
    private List<UserSelectableClass> users = new ArrayList<>();

    SessionManager sessionManager;

    public OnUserClickListener getOnUserClickListener() {
        return onUserClickListener;
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public OnUserClickListener onUserClickListener;
    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        return new UserListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_userlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        UserSelectableClass user = users.get(position);

        if (user.seatItem.getUserId().equals(sessionManager.getUser().getId())) {
            // Postpone the removal of the item until RecyclerView finishes computing the layout
            holder.itemView.post(new Runnable() {
                @Override
                public void run() {
                    // Remove the item from the list and notify the adapter
                    users.remove(position);
                    notifyDataSetChanged();
                }
            });
            return;
        }

        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addData(List<UserSelectableClass> users) {

        this.users = users;
    }

    public List<UserSelectableClass> getUsers() {
        return users;
    }

    public void selectAll() {
        users.forEach(user -> user.setSelected(true));
        notifyDataSetChanged();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        ItemGiftUserlistBinding binding;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemGiftUserlistBinding.bind(itemView);
        }

        public void setData(int position) {
            itemView.setVisibility(View.VISIBLE);

            UserSelectableClass user = users.get(position);
            Glide.with(binding.getRoot()).load(user.getSeatItem().getImage()).circleCrop().into(binding.imgview);
            binding.tvName.setText(user.getSeatItem().getName());

            if (user.isSelected()) {
                //  binding.imgview.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_round_pink_line));
                binding.tvName.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.purple));

            } else {
                //binding.imgview.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_round_pink));
                binding.tvName.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graylight));
            }
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position >= 0 && position < users.size()) {
                        users.get(position).setSelected(!user.isSelected());

                        onUserClickListener.onUserClick(users.get(position));
                        notifyItemChanged(position);
                    }
                }
            });
        }
    }
public  interface OnUserClickListener{
        void onUserClick(UserSelectableClass userSelectableClass);
}

}
