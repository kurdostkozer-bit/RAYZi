package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomSheetAudioroomChangepasscodeBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetAudioRoomChangePasscode {

    private final BottomSheetDialog bottomSheetDialog;
    OnRoomPasscodeSubmitedListener listener;
    SessionManager sessionManager;


    public BottomSheetAudioRoomChangePasscode(Context context, PkAudioLiveUserRoot.UsersItem liveUser, OnRoomPasscodeSubmitedListener listener) {
        this.listener = listener;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomsheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomsheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetAudioroomChangepasscodeBinding audioroompasscodeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_audioroom_changepasscode, null, false);
        bottomSheetDialog.setContentView(audioroompasscodeBinding.getRoot());
        sessionManager = new SessionManager(context);
        bottomSheetDialog.show();


        audioroompasscodeBinding.tvSubmit.setOnClickListener(v -> {

            String roomPasscode = audioroompasscodeBinding.etPasscode.getText().toString();
            listener.OnRoomPasscodeSubmitted(roomPasscode);

            Call<RestResponse> call = RetrofitBuilder.create().updatePasscode(roomPasscode, liveUser.getLiveUserId());
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if (response.body() != null) {
                        if (response.body().isStatus()) {
                            Toast.makeText(context, "Room Passcode Changed..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {

                }
            });
            bottomSheetDialog.dismiss();


//            String roompasscode = audioroompasscodeBinding.etPasscode.getText().toString();
//            listener.OnRoomPasscodeSubmitted(roompasscode);
//
//            if (roompasscode.isEmpty()) {
//                Toast.makeText(context, R.string.please_enter_room_welcome_message, Toast.LENGTH_SHORT).show();
//            } else {
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
//                    jsonObject.put("privateCode", roompasscode);
//                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_ROOMPASSCODE, jsonObject);
//                    Log.d("TAG", "BottomSheetAudioRoomChangePasscode: emitDone");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            bottomSheetDialog.dismiss();

        });

    }

    public interface OnRoomPasscodeSubmitedListener {
        void OnRoomPasscodeSubmitted(String Roompasscode);
    }
}
