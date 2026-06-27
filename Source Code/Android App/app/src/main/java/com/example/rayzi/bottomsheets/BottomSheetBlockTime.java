package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetBlockBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetBlockTime {

    private final BottomSheetDialog bottomSheetDialog;

    public BottomSheetBlockTime(Context context, BlockTimeListner blockTimeListner) {
       bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetBlockBinding bottomSheetBlock = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_block, null, false);
        bottomSheetDialog.setContentView(bottomSheetBlock.getRoot());
        bottomSheetDialog.show();

        bottomSheetBlock.tvOneHour.setOnClickListener(v -> {
            blockTimeListner.onehour("1 hour");
            bottomSheetDialog.dismiss();
        });

        bottomSheetBlock.tvOneDay.setOnClickListener(v -> {
            blockTimeListner.oneday("1 day");
            bottomSheetDialog.dismiss();
        });

        bottomSheetBlock.tvLifeTime.setOnClickListener(v -> {
            blockTimeListner.lifetime("lifetime");
            bottomSheetDialog.dismiss();
        });

    }

    public interface BlockTimeListner {

        void onehour(String blockUntil);

        void oneday(String blockUntil);

        void lifetime(String blockUntil);

    }
}
