package com.example.rayzi.user.wallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemCoinSellerBinding;
import com.example.rayzi.modelclass.CoinSellerRoot;

import java.util.ArrayList;
import java.util.List;

public class CoinSellerListAdapter extends RecyclerView.Adapter<CoinSellerListAdapter.CoinViewHolder> {

    OnCoinSellerClickListner onCoinSellerClickListner;
    private Context context;
    private List<CoinSellerRoot.CoinSellerItem> coinSellerList = new ArrayList<>();

    public OnCoinSellerClickListner getOnCoinPlanClickListener() {
        return onCoinSellerClickListner;
    }

    public void setOnCoinPlanClickListener(OnCoinSellerClickListner onCoinSellerClickListner) {
        this.onCoinSellerClickListner = onCoinSellerClickListner;
    }

    @Override
    public CoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_seller, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return coinSellerList.size();
    }

    public void addData(List<CoinSellerRoot.CoinSellerItem> coinList) {
        this.coinSellerList.addAll(coinList);
        notifyItemRangeInserted(this.coinSellerList.size(), coinList.size());
    }

    public void clearData() {
        this.coinSellerList.clear();
    }


    public interface OnCoinSellerClickListner {
        void onCoinSellerClick(CoinSellerRoot.CoinSellerItem coinPlan);
    }

    public class CoinViewHolder extends RecyclerView.ViewHolder {
        ItemCoinSellerBinding binding;

        public CoinViewHolder(View itemView) {
            super(itemView);
            binding = ItemCoinSellerBinding.bind(itemView);
        }

        public void setData(int position) {
            CoinSellerRoot.CoinSellerItem coinSellerItem = coinSellerList.get(position);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
            Glide.with(context).load(coinSellerItem.getImage()).apply(requestOptions).into(binding.profileCoinSeller);

            binding.nameCoinSeller.setText(coinSellerItem.getName());

            binding.getRoot().setOnClickListener(view -> {
                onCoinSellerClickListner.onCoinSellerClick(coinSellerItem);
            });
        }
    }
}
