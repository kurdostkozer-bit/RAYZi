package com.example.rayzi.user.wallet.coinseller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentSellerRechargeHistoryBinding;
import com.example.rayzi.databinding.ItemCoinHistoryBinding;
import com.example.rayzi.modelclass.CoinSellerHistoryRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerRechargeHistoryFragment extends BaseFragment {

    FragmentSellerRechargeHistoryBinding binding;

    public SellerRechargeHistoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_recharge_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTopUpHistory();
        binding.swipeRefresh.setOnRefreshListener(refreshLayout -> getTopUpHistory());

    }

    private void getTopUpHistory() {
        RetrofitBuilder.create().getTopupHistory(sessionManager.getUser().getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CoinSellerHistoryRoot> call, @NonNull Response<CoinSellerHistoryRoot> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().isStatus() && !response.body().getHistory().isEmpty()) {
                        binding.rvHistyory.setAdapter(new TopUpHistoryAdapter(response.body().getHistory()));
                        binding.swipeRefresh.finishRefresh();
                    } else {
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CoinSellerHistoryRoot> call, @NonNull Throwable t) {
                t.printStackTrace();
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    private class TopUpHistoryAdapter extends RecyclerView.Adapter<TopUpHistoryAdapter.TopUpViewHOlder> {
        private List<CoinSellerHistoryRoot.HistoryItem> history = new ArrayList<>();

        public TopUpHistoryAdapter(List<CoinSellerHistoryRoot.HistoryItem> history) {
            this.history = history;
        }

        @NonNull
        @Override
        public TopUpViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TopUpViewHOlder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_history, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TopUpViewHOlder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return history.size();
        }

        public class TopUpViewHOlder extends RecyclerView.ViewHolder {
            ItemCoinHistoryBinding binding;

            public TopUpViewHOlder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCoinHistoryBinding.bind(itemView);
            }

            @SuppressLint("SetTextI18n")
            public void setData(int position) {
                CoinSellerHistoryRoot.HistoryItem historyItem = history.get(position);

                binding.tvUserName.setVisibility(View.GONE);

                Glide.with(binding.getRoot()).load(historyItem.getImage()).circleCrop().into(binding.image);
                binding.tvAmount.setText("-" + historyItem.getCoin());
                binding.tvtime.setText(historyItem.getDate());
                binding.tvText.setText(historyItem.getName() + " (" + historyItem.getUniqueId() + ")");

            }

        }
    }
}