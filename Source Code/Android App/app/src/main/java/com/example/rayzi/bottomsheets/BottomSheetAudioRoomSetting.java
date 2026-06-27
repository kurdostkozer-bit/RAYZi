package com.example.rayzi.bottomsheets;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.SettingActivity;
import com.example.rayzi.databinding.BottomSheetAudioroomSettingsBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetAudioRoomSetting {

    private final BottomSheetDialog bottomSheetDialog;
    SessionManager sessionManager;

    @SuppressLint("SetTextI18n")
    public BottomSheetAudioRoomSetting(Context context, PkAudioLiveUserRoot.UsersItem liveUser, RoomSettingListener roomSettingListener) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetAudioroomSettingsBinding audioroomSettingsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_audioroom_settings, null, false);
        bottomSheetDialog.setContentView(audioroomSettingsBinding.getRoot());
        bottomSheetDialog.show();
        sessionManager = new SessionManager(context);

        if (liveUser.getPrivateCode() == 0) {
            audioroomSettingsBinding.tvTitleRoomPasscode.setVisibility(View.GONE);
            audioroomSettingsBinding.layPasscode.setVisibility(View.GONE);
        }else {
            audioroomSettingsBinding.tvTitleRoomPasscode.setVisibility(View.VISIBLE);
            audioroomSettingsBinding.layPasscode.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(liveUser.getRoomImage())
                .into(audioroomSettingsBinding.imgRoom);

        audioroomSettingsBinding.tvSeatCount.setText(liveUser.getSeat().size() + context.getString(R.string.people));
        audioroomSettingsBinding.tvName.setText(liveUser.getRoomName());
        audioroomSettingsBinding.tvWelcomemsg.setText(liveUser.getRoomWelcome());
        audioroomSettingsBinding.tvPassCode.setText((liveUser.getPrivateCode() != 0) ? String.valueOf(liveUser.getPrivateCode()) : "");

        audioroomSettingsBinding.tvName.setOnClickListener(view -> {
            roomSettingListener.onRoomNameChanged(audioroomSettingsBinding);
        });

        audioroomSettingsBinding.tvWelcomemsg.setOnClickListener(view -> {
            roomSettingListener.onRoomWelcomeMessageChanged(audioroomSettingsBinding);
        });

        audioroomSettingsBinding.imgEdit.setOnClickListener(view -> {
            roomSettingListener.onRoomImageChanged(audioroomSettingsBinding);
            bottomSheetDialog.dismiss();

        });

        audioroomSettingsBinding.ivEdit.setOnClickListener(v -> {
            roomSettingListener.onRoomPasscodeChanged(audioroomSettingsBinding);
        });

        audioroomSettingsBinding.layRoomClose.setOnClickListener(v -> {
//                new PopupBuilder(context).showReliteDiscardPopup("Are you sure you want to delete your room?", "","Yes", "No", () -> {
//                    RetrofitBuilder.create().deleteRoom(sessionManager.getUser().getId()).enqueue(new Callback<RestResponse>() {
//                        @Override
//                        public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
//                            Toast.makeText(context, "Your room deleted successfully..", Toast.LENGTH_SHORT).show();
//                            sessionManager.setIsAudioRoomBackground(false);
//                        }
//
//                        @Override
//                        public void onFailure(Call<RestResponse> call, Throwable t) {
//
//                        }
//                    });
//                });
                roomSettingListener.onRoomClose();
        });

        audioroomSettingsBinding.tvPassCode.setOnClickListener(view -> {
//            roomSettingListener.onRoomPasscodeChanged(audioroomSettingsBinding);

        });

        audioroomSettingsBinding.ivCopy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", audioroomSettingsBinding.tvPassCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        audioroomSettingsBinding.layWheat.setOnClickListener(v -> {
            roomSettingListener.onSeatSizeChanged(audioroomSettingsBinding);
            bottomSheetDialog.dismiss();
        });

        audioroomSettingsBinding.layChangeBg.setOnClickListener(view -> {
            roomSettingListener.onRoomBackgroundChanged();
            bottomSheetDialog.dismiss();
        });

        audioroomSettingsBinding.layBannedlist.setOnClickListener(v -> {
            roomSettingListener.onBannedUser();
            bottomSheetDialog.dismiss();
        });

    }

    public interface RoomSettingListener {

        void onRoomNameChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding);

        void onRoomImageChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding);

        void onSeatSizeChanged(BottomSheetAudioroomSettingsBinding audioroomSettingsBinding);

        void onRoomWelcomeMessageChanged(BottomSheetAudioroomSettingsBinding audioroomSettings);

        void onRoomPasscodeChanged(BottomSheetAudioroomSettingsBinding audioroomSettings);

        void onRoomBackgroundChanged();

        void onBannedUser();
        void onRoomClose();

    }
}
