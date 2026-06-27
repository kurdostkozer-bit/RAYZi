package com.example.rayzi.pk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemTimeBinding;

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.TImesViewHolder> {
    public OnTimeChooseLister onTimeChooseLister;
    int[] times = {1, 2, 5, 10};
    Context context;
    private int selectedPos = 0;

    public OnTimeChooseLister getOnTimeChooseLister() {
        return onTimeChooseLister;
    }

    public void setOnTimeChooseLister(OnTimeChooseLister onTimeChooseLister) {
        this.onTimeChooseLister = onTimeChooseLister;
    }

    @NonNull
    @Override
    public TImesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new TImesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TImesViewHolder holder, int position) {
        holder.binding.tvText.setText(times[position] + context.getString(R.string.minutes));

        if (selectedPos == position) {
            holder.binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.pink));
        } else {
            holder.binding.tvText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.graydark));
        }
        holder.binding.tvText.setOnClickListener(view -> {
            selectedPos = position;
            onTimeChooseLister.onTimeSelected(times[position]);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return times.length;
    }

    public interface OnTimeChooseLister {
        void onTimeSelected(int minutes);
    }

    public class TImesViewHolder extends RecyclerView.ViewHolder {
        ItemTimeBinding binding;

        public TImesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTimeBinding.bind(itemView);
        }
    }
}
