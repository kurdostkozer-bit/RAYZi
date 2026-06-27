package com.example.rayzi.pk.BottomSheets;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomsheetPkAnswerBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PkInvitationBottomSheet {
    private BottomsheetPkAnswerBinding binding;
    private final SessionManager sessionManager;
    private final BottomSheetDialog bottomSheetDialog;

    private final Context context;

    public PkInvitationBottomSheet(Context context) {
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

    public BottomSheetDialog pkBottomSheet(String title, String userImage, String host1_AvatarFrame_Image, OnPkInvitationClickLister onPopupClickListner) {

        if (context == null) return null;

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottomsheet_pk_answer, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());

        new CountDownTimer(10000, 1000) {
            @Override
            public void onFinish() {
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onTick(long l) {
                binding.timer.setText("00:0" + l / 1000);
            }
        }.start();

        binding.requestingHostImage.setUserImage(userImage, host1_AvatarFrame_Image, 20);
        binding.requestingHostName.setText(title);
        binding.acceptPkRequest.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            onPopupClickListner.onClickCountinue();
        });

        binding.rejectPkRequest.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            onPopupClickListner.onClickCancel();
        });

        return bottomSheetDialog;
    }

    public interface OnPkInvitationClickLister {
        void onClickCountinue();

        void onClickCancel();

    }

}
