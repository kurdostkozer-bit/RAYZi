package com.example.rayzi.liveGame.bottomsheet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomSheetGameListBinding;
import com.example.rayzi.liveGame.adapter.GameListAdapter;
import com.example.rayzi.modelclass.SettingRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;


public class BottomSheetGameList {

    private static final String TAG = "BottomSheetGameList";
    SessionManager sessionManager;

    public BottomSheetGameList(Context context, OnGameListLister gameListLister) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        sessionManager = new SessionManager(context);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetGameListBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_game_list, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();
        GameListAdapter gameListAdapter = new GameListAdapter();
        sheetDilogBinding.rvGameList.setAdapter(gameListAdapter);
        Log.d(TAG, "BottomSheetGameList: getGame ==" + sessionManager.getSetting().getGame().size());
        gameListAdapter.addData(sessionManager.getSetting().getGame());
        gameListAdapter.setClickGameList(gameItem -> {
            gameListLister.onClickGame(gameItem);
            bottomSheetDialog.dismiss();
        });


    }

    public interface OnGameListLister {
        void onClickGame(SettingRoot.Game gameItem);
    }
}
