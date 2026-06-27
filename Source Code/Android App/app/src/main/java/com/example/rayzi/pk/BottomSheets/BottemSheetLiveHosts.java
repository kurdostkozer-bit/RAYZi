package com.example.rayzi.pk.BottomSheets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.MyLoader;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.BottomSheetLiveHostsBinding;
import com.example.rayzi.databinding.ItemPkInviteHostBinding;
import com.example.rayzi.databinding.ItemVideoGridBinding;
import com.example.rayzi.liveStreamming.LiveListAdapter;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottemSheetLiveHosts {

    SessionManager sessionManager;
    BottomSheetDialog bottomSheetDialog;
    MyLoader myLoader = new MyLoader();

    LiveListAdapter liveListAdapter = new LiveListAdapter(LiveListAdapter.PKLIST_MODE);
    private final Context context;
    private int start = 0;
    private String keyword = "";

    public BottemSheetLiveHosts(Context context) {
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
    }

    public BottomSheetDialog openLiveHostListSheet(OnHostClickLister onHostClickLister) {
        if (context == null) return null;
        BottomSheetLiveHostsBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_live_hosts, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        sheetDilogBinding.setLoader(myLoader);
        bottomSheetDialog.show();
        sheetDilogBinding.rvHosts.setAdapter(liveListAdapter);
        getData(false, keyword, sheetDilogBinding);
        sheetDilogBinding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getData(false, keyword, sheetDilogBinding);
        });
        sheetDilogBinding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true, keyword, sheetDilogBinding);
        });

        liveListAdapter.setOnHostClickLister(new LiveListAdapter.OnHostClickLister() {
            @Override
            public void onHostItemClick(PkAudioLiveUserRoot.UsersItem userDummy, ItemVideoGridBinding itemVideoGridBinding, ItemPkInviteHostBinding itemPkInviteHostBinding) {
                onHostClickLister.onHostItemClick(userDummy, itemPkInviteHostBinding);
                bottomSheetDialog.dismiss();
            }

        });
        sheetDilogBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = s.toString();
                getData(false, keyword, sheetDilogBinding);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return bottomSheetDialog;
    }


    private void getData(boolean isLoadMore, String keyword, BottomSheetLiveHostsBinding sheetDilogBinding) {


        if (isLoadMore) {
            start = start + Const.LIMIT;

        } else {
            myLoader.isFristTimeLoading.set(true);
            start = 0;
            liveListAdapter.clear();

        }

        myLoader.noData.set(false);

        Call<PkAudioLiveUserRoot> call = RetrofitBuilder.create().getLiveUsersList(sessionManager.getUser().getId(), "PkRequest", keyword, start, Const.LIMIT);
        call.enqueue(new Callback<PkAudioLiveUserRoot>() {
            @Override
            public void onResponse(Call<PkAudioLiveUserRoot> call, Response<PkAudioLiveUserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        if (!response.body().getUsers().isEmpty()) {
                            liveListAdapter.addData(response.body().getUsers());
                        } else {
                            myLoader.noData.set(true);
                        }
                    }
                }
                myLoader.isFristTimeLoading.set(false);
                sheetDilogBinding.swipeRefresh.finishRefresh();
                sheetDilogBinding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<PkAudioLiveUserRoot> call, Throwable t) {

            }
        });
    }

    public interface OnHostClickLister {
        void onHostItemClick(PkAudioLiveUserRoot.UsersItem userDummy, ItemPkInviteHostBinding itemPkInviteHostBinding);

    }

}
