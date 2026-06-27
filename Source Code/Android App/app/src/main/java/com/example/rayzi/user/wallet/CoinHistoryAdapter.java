package com.example.rayzi.user.wallet;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemCoinHistoryBinding;
import com.example.rayzi.modelclass.HistoryListRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.user.guestUser.GuestActivity;

import java.util.ArrayList;
import java.util.List;

public class CoinHistoryAdapter extends RecyclerView.Adapter<CoinHistoryAdapter.CoinHistoryViewHolder> {

    private Context context;
    private int selectedPos = 0;
    private List<HistoryListRoot.HistoryItem> historyList = new ArrayList<>();
    private String coinType;

    @Override
    public CoinHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoinHistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_history, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinHistoryViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void addData(List<HistoryListRoot.HistoryItem> history) {

        this.historyList.addAll(history);
        notifyItemRangeInserted(this.historyList.size(), history.size());
    }

    public void setCoinType(String coinType) {

        this.coinType = coinType;
    }

    public void clear() {
        historyList.clear();
        notifyDataSetChanged();
    }

    public class CoinHistoryViewHolder extends RecyclerView.ViewHolder {
        ItemCoinHistoryBinding binding;

        public CoinHistoryViewHolder(View itemView) {
            super(itemView);
            binding = ItemCoinHistoryBinding.bind(itemView);
        }

        public void setData(int position) {
            HistoryListRoot.HistoryItem historyItem = historyList.get(position);
            Log.d("TAG", "setData: " + position + "          " + historyItem.toString());
            String amount;
            Log.d("TAG", "setData: diamond " + historyItem.getDiamond());

            if (historyItem.isIsAdd()) {
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
            }

            setTitleFromType(historyItem);

            amount = historyItem.isIsAdd() ? "+" : "-";
            if (coinType.equals(Const.DIAMOND)) {
                amount = amount + historyItem.getDiamond();
                Log.d("TAG", "setData: amountD " + amount);
            } else {
                amount = amount + historyItem.getRCoin();
                Log.d("TAG", "setData: amountR " + amount);
            }
            Log.d("TAG", "setData: amount " + amount);
            binding.tvAmount.setText(amount);

            binding.tvtime.setText(historyItem.getDate());


            binding.tvUserName.setText(Html.fromHtml("<font color='#FFFFFF'>by </font>@" + historyItem.getUserName()));


            if (historyItem.getUserName() != null && !historyItem.getUserName().isEmpty()) {
                binding.tvUserName.setVisibility(View.VISIBLE);
            } else {
                binding.tvUserName.setVisibility(View.GONE);
            }
            binding.tvUserName.setOnClickListener(v -> {
                context.startActivity(new Intent(context, GuestActivity.class).putExtra(Const.USERID, historyItem.getUserId()));
            });
        }

        private String setTitleFromType(HistoryListRoot.HistoryItem historyItem) {

            switch (historyItem.getType()) {
                case 0:

                    if (historyItem.getUserId() == null || historyItem.getUserId().isEmpty()) {
                        if (!historyItem.isIsAdd()) {
                            binding.tvText.setText(R.string.gift_broadcast_during_livestream_by_you);

                        }
                    } else if (historyItem.isIsAdd()) {
                        binding.tvText.setText(R.string.gift_received);
                        binding.tvUserName.setText("@" + historyItem.getUserName());
                    } else if (!historyItem.isIsAdd()) {
                        binding.tvText.setText(R.string.gift_send_to );
                        binding.tvUserName.setText("@" + historyItem.getUserName());
                    }
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gift));
                    break;
                case 1:
                    binding.tvText.setText(Const.CoinName + context.getString(R.string.converted_to_diamonds));
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rcoin));
                    break;
                case 2:
                    binding.tvText.setText(R.string.diamond_purchase);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    break;
                case 3:
                    binding.tvText.setText(R.string.you_was_call_to);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.videocall));
                    break;
                case 4:
                    binding.tvText.setText(R.string.watching_ads );
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ads));
                    break;
                case 5:
                    binding.tvText.setText(R.string.login_bonus);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.moneybag));
                    break;
                case 6:
                    binding.tvText.setText(R.string.referral_bonus );
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.moneybag));
                    break;
                case 7:
                    binding.tvText.setText(R.string.cash_out);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.withdraw));
                    break;
                case 8:
                    if (historyItem.isIsAdd()) {
                        binding.tvText.setText(R.string.added_by_admin);
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                    } else {
                        binding.tvText.setText(R.string.reduce_by_admin);
                        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    }
                    if (historyItem.getRCoin() == 0) {
                        binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    } else if (historyItem.getDiamond() == 0) {

                        binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rcoin));
                    }
                    break;

                case 9:
                    binding.tvText.setText(R.string.svga_animation_purchased);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 10:
                    binding.tvText.setText(R.string.add_in_teenpatti_game);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 11:
                    binding.tvText.setText(R.string.purchased_avatarframe );
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 12:
                    binding.tvText.setText(R.string.coin_add_by_offline_recharge);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                    break;

                case 13:
                    binding.tvText.setText(R.string.call_coin_received);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 14:
                    //not handle by backend
                    binding.tvText.setText(R.string.lucy_gift_bonus);
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    break;

                case 15:
                    binding.tvText.setText(R.string.add_in_roulette_game);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 16:
                    binding.tvText.setText(R.string.add_in_ferry_wheel_game);
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

                case 17:
                    // not handle by backend
                    binding.tvText.setText(R.string.convert_in_coin_seller_coin );
                    binding.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.diamond));
                    binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
                    break;

            }
            return "";
        }
    }
}
