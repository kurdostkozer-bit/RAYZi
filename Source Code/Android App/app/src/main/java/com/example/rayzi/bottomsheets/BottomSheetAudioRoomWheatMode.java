package com.example.rayzi.bottomsheets;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomsheetAudioroomWheatmodeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetAudioRoomWheatMode {

    private final BottomSheetDialog bottomSheetDialog;

    public BottomSheetAudioRoomWheatMode(Context context, int size, OnSeatClickListener onSeatClickListener) {

        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);

        BottomsheetAudioroomWheatmodeBinding audioroomSettingsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_audioroom_wheatmode, null, false);
        bottomSheetDialog.setContentView(audioroomSettingsBinding.getRoot());
        bottomSheetDialog.show();

        if (size == 8) {
            audioroomSettingsBinding.laySelect8.setBackgroundColor(Color.parseColor("#BA3A7E"));
            audioroomSettingsBinding.laySelect12.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.laySelect16.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.tvSeatSelect8.setText("Using");
        } else if (size == 12) {
            audioroomSettingsBinding.laySelect8.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.laySelect12.setBackgroundColor(Color.parseColor("#BA3A7E"));
            audioroomSettingsBinding.laySelect16.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.tvSeatSelect12.setText("Using");
        } else if (size == 15) {
            audioroomSettingsBinding.laySelect16.setBackgroundColor(Color.parseColor("#BA3A7E"));
            audioroomSettingsBinding.laySelect8.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.laySelect12.setBackgroundColor(Color.parseColor("#80000000"));
            audioroomSettingsBinding.tvSeatSelect16.setText("Using");
        }

        audioroomSettingsBinding.lay8Seat.setOnClickListener(v -> {
            onSeatClickListener.onSeatClick(8);
            bottomSheetDialog.dismiss();
        });

        audioroomSettingsBinding.lay12Seat.setOnClickListener(v -> {
            onSeatClickListener.onSeatClick(12);
            bottomSheetDialog.dismiss();
        });

        audioroomSettingsBinding.lay16Seat.setOnClickListener(v -> {
            onSeatClickListener.onSeatClick(15);
            bottomSheetDialog.dismiss();
        });

    }

    public interface OnSeatClickListener {
        void onSeatClick(int seatCount);
    }

}
