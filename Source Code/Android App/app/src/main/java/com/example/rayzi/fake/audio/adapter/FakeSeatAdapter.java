package com.example.rayzi.fake.audio.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.fake.audio.model.SeatModalClass;

import java.util.ArrayList;
import java.util.List;

public class FakeSeatAdapter extends RecyclerView.Adapter<FakeSeatAdapter.MyViewHolderClass> {
    public static int pos;
    onSeatClick onSeatClicklistioner;
    List<SeatModalClass> seatlist = new ArrayList<>();
    Context context;
    ontakeseat ontakeseatlistener;

    public void setOntakeseatlistener(ontakeseat ontakeseatlistener) {
        this.ontakeseatlistener = ontakeseatlistener;
    }

    public ontakeseat getOntakeseatlistener() {
        return ontakeseatlistener;
    }

    @NonNull
    @Override
    public MyViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new MyViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderClass holder, int position) {
        Glide.with(holder.binding.image).load(seatlist.get(position).getImage()).placeholder(R.drawable.audio_seat).circleCrop().into(holder.binding.userImage);

        if (!seatlist.get(position).getName().isEmpty()) {
            holder.binding.nameCount.setText(seatlist.get(position).getName());
        } else {
            holder.binding.nameCount.setText(seatlist.get(position).getSeat_id());
        }

        holder.itemView.setOnClickListener(v -> {
            onSeatClicklistioner.onSeatClicklis(seatlist.get(position));
        });

        holder.setData(position);

    }


    @Override
    public int getItemCount() {
        return seatlist.size();
    }

    public void adddata(List<SeatModalClass> seatList1) {
        seatlist.addAll(seatList1);
        notifyItemRangeInserted(seatlist.size(), seatList1.size());
    }


    public void updateData(List<SeatModalClass> seatList) {
        seatlist.clear();
        seatlist.addAll(seatList);
        notifyDataSetChanged();
    }

    public List<SeatModalClass> getSeatList() {
        return seatlist;
    }


    public interface onSeatClick {
        void onSeatClicklis(SeatModalClass seatModalClass);
    }

    public class MyViewHolderClass extends RecyclerView.ViewHolder {

        ItemSeatBinding binding;

        public MyViewHolderClass(@NonNull View itemView) {
            super(itemView);
            binding = ItemSeatBinding.bind(itemView);


        }

        public void setData(int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("TAG", "onClick: >>>>>>>>>>>>>>>>>>>>>>>> ");

                    ontakeseatlistener.onClickseat(seatlist.get(position), position, binding);
                }
            });
        }
    }

    public interface ontakeseat {
        void onClickseat(SeatModalClass seatModalClass, int position, ItemSeatBinding binding);
    }
}
