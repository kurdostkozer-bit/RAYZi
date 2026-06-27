package com.example.rayzi.bottomsheets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rayzi.R;
import com.example.rayzi.adapter.BannedListAdapter;
import com.example.rayzi.databinding.BottomSheetBannedListBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;

public class BottomSheetBannedList {

    private final BottomSheetDialog bottomSheetDialog;
    BannedListAdapter bannedListAdapter;


    public BottomSheetBannedList(Context context, JSONArray jsonArray, OnclickListener onclickListener) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetBannedListBinding bottomSheetBannedList = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_banned_list, null, false);
        bottomSheetDialog.setContentView(bottomSheetBannedList.getRoot());
        bottomSheetDialog.show();

        bottomSheetBannedList.rvBannedList.setLayoutManager(new LinearLayoutManager(context));
        bannedListAdapter = new BannedListAdapter(context, new BannedListAdapter.onUnblockListener() {
            @Override
            public void onUnblock(String id, int position) {
                onclickListener.onUnblockclick(id);
                bottomSheetDialog.dismiss();
                jsonArray.remove(position);
                Toast.makeText(context, "Unblocked Successfully!!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("TAG", "BottomSheetBannedList: =====" + jsonArray);

        if (jsonArray != null && jsonArray.length() != 0){
            bannedListAdapter.addData(jsonArray);

        }

        if (jsonArray == null || jsonArray.length() == 0){
//            bottomSheetBannedList.rvBannedList.setVisibility(View.GONE);
            bottomSheetBannedList.tvNodataFound.setVisibility(View.VISIBLE);
        }else {
            bottomSheetBannedList.rvBannedList.setAdapter(bannedListAdapter);

//            bottomSheetBannedList.rvBannedList.setVisibility(View.VISIBLE);
            bottomSheetBannedList.tvNodataFound.setVisibility(View.GONE);
        }



    }

    public interface OnclickListener{

        void onUnblockclick(String id);

    }


}
