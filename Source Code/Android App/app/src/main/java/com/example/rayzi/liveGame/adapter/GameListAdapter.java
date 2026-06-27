package com.example.rayzi.liveGame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemGameListBinding;
import com.example.rayzi.modelclass.SettingRoot;

import java.util.ArrayList;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {
    List<SettingRoot.Game> gameList = new ArrayList<>();
    Context context;
    onClickGameList clickGameList;

    @NonNull
    @Override
    public GameListAdapter.GameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new GameListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_game_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GameListAdapter.GameListViewHolder holder, int position) {
        holder.setData(gameList.get(position));
    }

    public void addData(List<SettingRoot.Game> gameList) {
        this.gameList.addAll(gameList);
        notifyItemRangeInserted(this.gameList.size(), gameList.size());
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void setClickGameList(onClickGameList clickGameList) {
        this.clickGameList = clickGameList;
    }

    public class GameListViewHolder extends RecyclerView.ViewHolder {
        private ItemGameListBinding binding;

        public GameListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemGameListBinding.bind(itemView);
        }

        public void setData(SettingRoot.Game gameItem) {
            Glide.with(context).load(gameItem.getImage()).into(binding.image);
            binding.gameName.setText(gameItem.getName());

            binding.getRoot().setOnClickListener(v -> clickGameList.onClickGame(gameItem));
        }
    }

    public interface onClickGameList {
        void onClickGame(SettingRoot.Game gameItem);

    }
}
