package com.example.rayzi.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemCoinHistoryBinding;
import com.example.rayzi.databinding.ItemMyrecordsBinding;
import com.example.rayzi.modelclass.HistoryListRoot;
import com.example.rayzi.modelclass.RecordRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.user.guestUser.GuestActivity;

import java.util.ArrayList;
import java.util.List;

public class MyRecordAdapter extends RecyclerView.Adapter<MyRecordAdapter.CoinHistoryViewHolder> {

    private Context context;
    private int selectedPos = 0;
    private List<RecordRoot.History> historyList = new ArrayList<>();
    private String coinType;


    @Override
    public CoinHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoinHistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myrecords, parent, false));
    }

    @Override
    public void onBindViewHolder(CoinHistoryViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void addData(List<RecordRoot.History> history) {

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
        ItemMyrecordsBinding binding;

        public CoinHistoryViewHolder(View itemView) {
            super(itemView);
            binding = ItemMyrecordsBinding.bind(itemView);
        }

        public void setData(int position) {
            RecordRoot.History historyItem = historyList.get(position);
            Log.d("TAG", "setData: " + position + "          " + historyItem.toString());
            int tintColorWhite = ContextCompat.getColor(context, R.color.white);
            Drawable drawable = binding.image.getDrawable();
            if (historyItem.isAudio()) {
                binding.tvType.setText(R.string.audio_streaming);
                binding.image.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.purple));
                binding.image.setImageResource(R.drawable.mike);
            } else {
                binding.tvType.setText(R.string.live_streaming);
                binding.image.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
                binding.image.setImageResource(R.drawable.live);
            }

            binding.tvViews.setText(context.getString(R.string.views)  + historyItem.getUser());
            binding.tvtime.setText(historyItem.getStartTime());
            binding.tvDuration.setText(historyItem.getDuration());
            //   Log.d("TAG", "setData: diamond " + historyItem.getDiamond());





          /*  amount = historyItem.isIsAdd() ? "+" : "-";
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
            }*/

        }

    }
}
