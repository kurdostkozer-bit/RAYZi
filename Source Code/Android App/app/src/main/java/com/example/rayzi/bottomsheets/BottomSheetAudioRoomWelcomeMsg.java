package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomsheetAudiorromWelcomemsgBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.socket.MySocketManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class BottomSheetAudioRoomWelcomeMsg {

    private final BottomSheetDialog bottomSheetDialog;
    private final OnWelcomeMessageSubmittedListener listener;
    SessionManager sessionManager;

    public BottomSheetAudioRoomWelcomeMsg(Context context, PkAudioLiveUserRoot.UsersItem liveUser, OnWelcomeMessageSubmittedListener listener) {
        this.listener = listener;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomsheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomsheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomsheetAudiorromWelcomemsgBinding audioroomWelcomemsgBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_audiorrom_welcomemsg, null, false);
        bottomSheetDialog.setContentView(audioroomWelcomemsgBinding.getRoot());
        sessionManager = new SessionManager(context);
        bottomSheetDialog.show();

        audioroomWelcomemsgBinding.tvSubmit.setOnClickListener(v -> {
            String wlcmessage = audioroomWelcomemsgBinding.etName.getText().toString();
            listener.OnWelcomeMessageSubmitted(wlcmessage);

            if (wlcmessage.isEmpty()) {
                Toast.makeText(context, R.string.please_enter_room_welcome_message, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                    jsonObject.put("roomWelcome", wlcmessage);
                    MySocketManager.getInstance().getSocket().emit(Const.EVENT_ROOMWELCOME, jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bottomSheetDialog.dismiss();

        });

    }

    public interface OnWelcomeMessageSubmittedListener {
        void OnWelcomeMessageSubmitted(String WlcMessage);
    }

}
