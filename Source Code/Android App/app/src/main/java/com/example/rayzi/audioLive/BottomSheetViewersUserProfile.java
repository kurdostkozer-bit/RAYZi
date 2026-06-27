package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetOnlineProfileBinding;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetViewersUserProfile {

    BottomSheetDialog bottomSheetDialog;
    Context context;

    public BottomSheetViewersUserProfile(Context context, PkAudioLiveUserRoot.UsersItem.SeatItem seatItem, GuestProfileRoot.User userData, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        BottomSheetOnlineProfileBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_online_profile, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.userImg.setUserImage(userData.getImage(), userData.getAvatarFrameImage(), 20);

        sheetDilogBinding.userName.setText(userData.getName());
        sheetDilogBinding.userId.setText(userData.getUniqueId());
        sheetDilogBinding.gender.setText(userData.getGender());
        sheetDilogBinding.location.setText(userData.getCountry());
        sheetDilogBinding.tvLevel.setText(userData.getLevel().getName());

        if (userData.isHost()) {
            sheetDilogBinding.layType.setVisibility(View.VISIBLE);
            sheetDilogBinding.ivType.setImageResource(R.drawable.ic_user_place);
            sheetDilogBinding.tvType.setText("Creator");
        } else if (userData.isAgency()) {
            sheetDilogBinding.layType.setVisibility(View.VISIBLE);
            sheetDilogBinding.ivType.setImageResource(R.drawable.ic_agency);
            sheetDilogBinding.tvType.setText("Agency");
        } else {
            sheetDilogBinding.layType.setVisibility(View.GONE);
        }

        if (userData.isVIP()) {
            sheetDilogBinding.layType.setVisibility(View.VISIBLE);
            sheetDilogBinding.ivType.setImageResource(R.drawable.crown);
        } else {
            sheetDilogBinding.layType.setVisibility(View.GONE);
        }

        if (userData.isCoinSeller()) {
            sheetDilogBinding.layCoinType.setVisibility(View.VISIBLE);
            sheetDilogBinding.ivCoinType.setImageResource(R.drawable.ic_agency);
            sheetDilogBinding.tvCoinType.setText("Coin Seller");
        }
        else {
            sheetDilogBinding.layCoinType.setVisibility(View.GONE);
        }


        sheetDilogBinding.unMuteMic.setOnClickListener(v -> {
            onClickListner.onUnMute(sheetDilogBinding);
            bottomSheetDialog.dismiss();
        });

        sheetDilogBinding.removeSit.setOnClickListener(v -> {
            if (seatItem.isReserved()) {
                onClickListner.onRemoveSeat();
            } else {
                onClickListner.inviteUser();
            }
            bottomSheetDialog.dismiss();
        });

        sheetDilogBinding.kickOut.setOnClickListener(v -> {
            onClickListner.onkickOut();
            bottomSheetDialog.dismiss();
        });

        if (seatItem.isMute() == 1 || seatItem.isMute() == 2) {
            sheetDilogBinding.txtMic.setText(R.string.unmute_mic);
            Glide.with(context).load(R.drawable.speaker_off).into(sheetDilogBinding.mute);
        } else {
            sheetDilogBinding.txtMic.setText(R.string.mute_mic);
            Glide.with(context).load(R.drawable.speaker).into(sheetDilogBinding.mute);
        }

        if (seatItem.isReserved()) {
            sheetDilogBinding.txtSeat.setText(R.string.remove_mic);
            Glide.with(context).load(R.drawable.remove_sit).into(sheetDilogBinding.seat);
        } else {
            sheetDilogBinding.txtSeat.setText(R.string.invite_mic);
            Glide.with(context).load(R.drawable.take_sit).into(sheetDilogBinding.seat);
        }


    }

    public interface OnClickListner {
        void onUnMute(BottomSheetOnlineProfileBinding sheetDilogBinding);

        void onRemoveSeat();

        void onkickOut();

        void inviteUser();
    }


}
