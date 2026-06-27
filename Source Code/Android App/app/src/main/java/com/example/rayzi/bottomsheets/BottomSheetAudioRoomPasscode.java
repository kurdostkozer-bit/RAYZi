package com.example.rayzi.bottomsheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomsheetAudiorromPasscodeUpdateBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetAudioRoomPasscode {

    private final BottomSheetDialog bottomSheetDialog;
    SessionManager sessionManager;

    @SuppressLint("SetTextI18n")
    public BottomSheetAudioRoomPasscode(Context context, PkAudioLiveUserRoot.UsersItem liveUser, OnWelcomeMessageSubmittedListener listener) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomsheet = d.findViewById(R.id.design_bottom_sheet);
            assert bottomsheet != null;
            BottomSheetBehavior.from(bottomsheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomsheetAudiorromPasscodeUpdateBinding audioroomWelcomemsgBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_audiorrom_passcode_update, null, false);
        bottomSheetDialog.setContentView(audioroomWelcomemsgBinding.getRoot());
        sessionManager = new SessionManager(context);
        bottomSheetDialog.show();

        audioroomWelcomemsgBinding.tvRoomName.setText(liveUser.getRoomName());

        if (liveUser.isIsFake()) {
            audioroomWelcomemsgBinding.etPasscode.setText(R.string._123456);
        }

        audioroomWelcomemsgBinding.btnSubmit.setOnClickListener(v -> {
            if (!audioroomWelcomemsgBinding.etPasscode.getText().toString().isEmpty()) {
                if (liveUser.getPrivateCode() == Integer.parseInt(audioroomWelcomemsgBinding.etPasscode.getText().toString())) {
                    listener.OnWelcomeMessageSubmitted(Integer.parseInt(audioroomWelcomemsgBinding.etPasscode.getText().toString()));
                    bottomSheetDialog.dismiss();
                } else {
                    audioroomWelcomemsgBinding.etPasscode.setError(context.getString(R.string.enter_valid_passcode));
                }
            } else {
                audioroomWelcomemsgBinding.etPasscode.setError(context.getString(R.string.enter_passcode));
            }
        });

        audioroomWelcomemsgBinding.ivClose.setOnClickListener(view -> {
            listener.OnWelcomeMessageSubmitted(0);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                listener.OnWelcomeMessageSubmitted(0);
                bottomSheetDialog.dismiss();
            }
        });

        audioroomWelcomemsgBinding.btnSubmit.setOnClickListener(v -> {
            if (!audioroomWelcomemsgBinding.etPasscode.getText().toString().isEmpty()) {
                if (liveUser.getPrivateCode() == Integer.parseInt(audioroomWelcomemsgBinding.etPasscode.getText().toString())) {
                    listener.OnWelcomeMessageSubmitted(Integer.parseInt(audioroomWelcomemsgBinding.etPasscode.getText().toString()));
                    bottomSheetDialog.dismiss();
                } else {
                    audioroomWelcomemsgBinding.etPasscode.setError(context.getString(R.string.enter_valid_passcode));
                }
            } else {
                audioroomWelcomemsgBinding.etPasscode.setError(context.getString(R.string.enter_passcode));
            }
        });

    }

    public interface OnWelcomeMessageSubmittedListener {
        void OnWelcomeMessageSubmitted(int privateCode);
    }

}
