package com.example.rayzi.bottomsheets;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.adapter.BlockedUserListAdapter;
import com.example.rayzi.databinding.BottomSheetBannedListBinding;
import com.example.rayzi.databinding.BottomSheetBlockedUserListBinding;
import com.example.rayzi.modelclass.BlockedUserListRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.retrofit.UserApiCall;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetBlockedUserList {

    private final BottomSheetDialog bottomSheetDialog;
    BlockedUserListAdapter blockedUserListAdapter;
    UserApiCall userApiCall;
    SessionManager sessionManager;
    List<BlockedUserListRoot.BlockedUsersItem> blockuserList = new ArrayList<>();

    public BottomSheetBlockedUserList(Context context) {

        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetBlockedUserListBinding bottomSheetBlockedList = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_blocked_user_list, null, false);
        bottomSheetDialog.setContentView(bottomSheetBlockedList.getRoot());
        bottomSheetDialog.show();
        userApiCall = new UserApiCall(context);
        sessionManager = new SessionManager(context);


        bottomSheetBlockedList.rvBannedList.setLayoutManager(new LinearLayoutManager(context));

        bottomSheetBlockedList.progressbar.setVisibility(VISIBLE);
        Call<BlockedUserListRoot> call = RetrofitBuilder.create().getBlockUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<BlockedUserListRoot>() {

            @Override
            public void onResponse(Call<BlockedUserListRoot> call, Response<BlockedUserListRoot> response) {
                if (response.body() != null && response.isSuccessful()){
                    bottomSheetBlockedList.progressbar.setVisibility(GONE);
                    blockuserList = response.body().getBlockedUsers();
                    blockedUserListAdapter.addData(blockuserList);
                    bottomSheetBlockedList.rvBannedList.setAdapter(blockedUserListAdapter);

                    if (blockuserList.isEmpty()){
                        bottomSheetBlockedList.tvNodataFound.setVisibility(VISIBLE);
                    }else {
                        bottomSheetBlockedList.tvNodataFound.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BlockedUserListRoot> call, Throwable t) {

            }
        });


        blockedUserListAdapter = new BlockedUserListAdapter(context, new BlockedUserListAdapter.onUnblockListener() {
            @Override
            public void onUnblock(String id, int position) {

                userApiCall.blockUnblock(id, new UserApiCall.OnBlockUnblockListner() {
                    @Override
                    public void onBlockSuccess() {

                    }

                    @Override
                    public void onUnblockSuccess() {
                        blockedUserListAdapter.removeItem(position);
                        Toast.makeText(context, "Unblocked Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
