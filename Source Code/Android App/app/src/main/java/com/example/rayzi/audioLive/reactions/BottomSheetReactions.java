package com.example.rayzi.audioLive.reactions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetReactionsBinding;
import com.example.rayzi.modelclass.ReactionRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class BottomSheetReactions {

    private final BottomSheetDialog bottomSheetDialog;
    private final BottomSheetReactionsBinding bottomSheetReactionsBinding;
    public OnReactionClickListner onReactionClickListner;
    ReactionGridAdapter reactionGridAdapter = new ReactionGridAdapter();

    public BottomSheetReactions(Context context) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        bottomSheetReactionsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_reactions, null, false);
        bottomSheetDialog.setContentView(bottomSheetReactionsBinding.getRoot());
        // bottomSheetDialog.show();
        bottomSheetReactionsBinding.rvEmoji.setAdapter(reactionGridAdapter);

        reactionGridAdapter.setonReactionClickListner(reaction -> {
            onReactionClickListner.onReactionClick(reaction);
            bottomSheetDialog.dismiss();
        });
    }

    public OnReactionClickListner getOnReactionClickListner() {
        return onReactionClickListner;
    }

    public void setOnReactionClickListner(OnReactionClickListner onReactionClickListner) {
        this.onReactionClickListner = onReactionClickListner;
    }

    public void loadData(List<ReactionRoot.DataItem> data) {
        Log.d("TAG", "loadData: ");

        reactionGridAdapter.addData(data);

    }


    public void show() {
        bottomSheetDialog.show();
    }
}
