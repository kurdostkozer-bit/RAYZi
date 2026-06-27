package com.example.rayzi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemRvprofileBinding;
import com.example.rayzi.modelclass.ProfileRoot;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.myViewholder> {

    private List<ProfileRoot> profileRootList = new ArrayList<>();
    OnItemClickListener onItemClickListener;

    public ProfileAdapter() {
    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rvprofile, parent, false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
        ProfileRoot item = profileRootList.get(position);
        holder.binding.ivAgency.setImageResource(item.getGetImages());
        holder.binding.tvAgency.setText(item.getGetText());

        holder.itemView.setOnClickListener(view -> {
            onItemClickListener.onClick(item.getGetText());
        });

    }

    public void addData(List<ProfileRoot> profileRootList){
        this.profileRootList.addAll(profileRootList);
        notifyDataSetChanged();
    }

    public void setOnClickListener(@NonNull OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return profileRootList.size();
    }

    public class myViewholder extends RecyclerView.ViewHolder {
        ItemRvprofileBinding binding;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRvprofileBinding.bind(itemView);
        }
    }

    public interface OnItemClickListener {

        void onClick(String type);

    }

}
