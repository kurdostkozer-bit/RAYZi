package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetViewersOnlineBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class BottomSheetViewersUsers {

    public LiveViewUserAdapter liveViewUserAdapter = new LiveViewUserAdapter();
    BottomSheetDialog bottomSheetDialog;
    BakgroundAdapter bakgroundAdapter;
    Context context;

    public BottomSheetViewersUsers(Context context, JSONArray list, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        BottomSheetViewersOnlineBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_viewers_online, null, false);

        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.rvViewUsers.setAdapter(liveViewUserAdapter);

        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter(userDummy -> {
            onClickListner.OnItemClick(userDummy);
            bottomSheetDialog.dismiss();
        });

        liveViewUserAdapter.addData(list);

    }

    public interface OnClickListner {
        void OnItemClick(JSONObject userDummy);
    }

}
