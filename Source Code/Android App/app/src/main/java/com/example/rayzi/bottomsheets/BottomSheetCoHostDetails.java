package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.adapter.CoHostListAdapter;
import com.example.rayzi.audioLive.SeatItem;
import com.example.rayzi.databinding.BottomSheetCohostDetailBinding;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;


public class BottomSheetCoHostDetails {

    private final BottomSheetDialog bottomSheetDialog;
    private final Context context;
    SessionManager sessionManager;

    public BottomSheetCoHostDetails(Context context, ArrayList<PkAudioLiveUserRoot.UsersItem.SeatItem> seatItem, OnCommentDetailClickLister onCommentDetailClickLister) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;
        sessionManager = new SessionManager(context);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetCohostDetailBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_cohost_detail, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        sheetDilogBinding.rvCoHostList.setLayoutManager(layoutManager);
        CoHostListAdapter adapter = new CoHostListAdapter();
        sheetDilogBinding.rvCoHostList.setAdapter(adapter);
        adapter.addData(seatItem);
        adapter.setCoHostClickListener((position, seatItem1) -> {

            onCommentDetailClickLister.onClickUnsend(seatItem1.getUserId());
            bottomSheetDialog.dismiss();

        });

    }

    public interface OnCommentDetailClickLister {
        void onClickUnsend(String id);
    }
}
