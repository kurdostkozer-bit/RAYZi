package com.example.rayzi.pk.BottomSheets;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomsheetWaitingResponseForPkBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PkWaitingForResponseBottomSheet {
    private BottomsheetWaitingResponseForPkBinding binding;
    private final SessionManager sessionManager;
    private final BottomSheetDialog bottomSheetDialog;

    private final Context context;

    public PkWaitingForResponseBottomSheet(Context context) {
        this.context = context;

        sessionManager = new SessionManager(context);

        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

    }

    public BottomSheetDialog pkBottomSheet(PkAudioLiveUserRoot.UsersItem hostLocal, PkAudioLiveUserRoot.UsersItem hostRemote, onPkWaitingResponseListener pkWaitingResponseListener) {

        if (context == null) return null;

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_waiting_response_for_pk, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());


        binding.localHostImage.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 23);
        binding.remoteHostImage.setUserImage(hostRemote.getImage(), hostRemote.getAvatarFrameImage(), 23);
        binding.localHostName.setText(hostLocal.getName());
        binding.remoteHostName.setText(hostRemote.getName());

        binding.pkRequestCutByHost.setOnClickListener(view -> {
            pkWaitingResponseListener.onCutWaitingByHostClick();
            bottomSheetDialog.dismiss();
        });
        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long l) {
                binding.pendingTimer.setText("00:0" + l / 1000);
            }

            @Override
            public void onFinish() {
                bottomSheetDialog.dismiss();
            }
        }.start();
        if (!bottomSheetDialog.isShowing()) {
            bottomSheetDialog.show();
        }
        return bottomSheetDialog;
    }

    public interface onPkWaitingResponseListener {
        void onCutWaitingByHostClick();

    }

}
