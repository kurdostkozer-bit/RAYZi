package com.example.rayzi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemSvgaListBinding;
import com.example.rayzi.modelclass.SvgaListRoot;

import java.util.ArrayList;
import java.util.List;

public class SvgaListAdapter extends RecyclerView.Adapter<SvgaListAdapter.SvgaViewHolder> {
    List<SvgaListRoot.DataItem> svgaItemList = new ArrayList<>();
    Context context;
    SessionManager sessionManager;
    onSvgaClickListener onSvgaClickListener;
    private String TAG = "SvgaListAdapter";

    public void setOnSvgaClickListener(SvgaListAdapter.onSvgaClickListener onSvgaClickListener) {
        this.onSvgaClickListener = onSvgaClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public SvgaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        return new SvgaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_svga_list, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull SvgaViewHolder holder, int position) {
        holder.setData(svgaItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return svgaItemList.size();
    }

    public class SvgaViewHolder extends RecyclerView.ViewHolder {
        ItemSvgaListBinding binding;

        public SvgaViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSvgaListBinding.bind(itemView);
        }

        public void setData(SvgaListRoot.DataItem svgaItem) {
            binding.svgaName.setText(svgaItem.getName());
            binding.priceDiamonds.setText(String.valueOf(svgaItem.getDiamond()));
            binding.validationTag.setText(" /" + svgaItem.getValidationTag());
            Glide.with(context).load(BuildConfig.BASE_URL + svgaItem.getThumbnail()).into(binding.svgImage);

            Log.d(TAG, "setData: svgaItem.isIsPurchase() ====== " + svgaItem.isIsPurchase());
            Log.d(TAG, "setData: svgaItem.isIsSelected() ====== " + svgaItem.isIsSelected());
            if (svgaItem.isIsPurchase())
                binding.btnpurchase.setImageResource(R.drawable.select_btn);
            if (svgaItem.isIsSelected())
                binding.btnpurchase.setImageResource(R.drawable.selected_btn);

            // click
            binding.btnpurchase.setOnClickListener(v -> {

                if (svgaItem.isIsPurchase() && !svgaItem.isIsSelected()) {
                    for (int i = 0; i < svgaItemList.size(); i++) {
                        if (svgaItemList.get(i).isIsSelected()) {
                            svgaItemList.get(i).setSelected(false);
                        }
                    }
                }
                onSvgaClickListener.onPurchaseClick(svgaItem, binding);
            });

            binding.svgImage.setOnClickListener(v -> onSvgaClickListener.onSvgaClick(svgaItem));
        }
    }

    public interface onSvgaClickListener {
        void onPurchaseClick(SvgaListRoot.DataItem svgaItem, ItemSvgaListBinding binding);

        void onSvgaClick(SvgaListRoot.DataItem svgaItem);
    }

    public void addData(List<SvgaListRoot.DataItem> svgaItemList) {
        this.svgaItemList.addAll(svgaItemList);
        notifyItemRangeInserted(this.svgaItemList.size(), svgaItemList.size());
    }

    public void clear() {
        svgaItemList.clear();
        notifyDataSetChanged();
    }
}
