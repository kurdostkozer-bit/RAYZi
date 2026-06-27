package com.example.rayzi.demoreels;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityDemoReelsBinding;
import com.example.rayzi.modelclass.ReliteRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DemoReelsActivity extends BaseActivity {
    private ActivityDemoReelsBinding binding;
    DemoReelsAdapter demoReelsAdapter = new DemoReelsAdapter();
    private int start = 0;
    private VideoAutoPlayHelper videoAutoPlayHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_reels);
        getData(false);
        binding.rvReels.setAdapter(demoReelsAdapter);
        new PagerSnapHelper().attachToRecyclerView(binding.rvReels);
        videoAutoPlayHelper = new VideoAutoPlayHelper(binding.rvReels);
        binding.rvReels.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                videoAutoPlayHelper.onScrolled(true);
            }
        });
        videoAutoPlayHelper.startObserving();
    }

    private void getData(boolean isLoadMore) {
        if (isLoadMore) {
            start += Const.LIMIT;
        } else {
            demoReelsAdapter.clear();
        }
        Call<ReliteRoot> call = RetrofitBuilder.create().getRelites(sessionManager.getUser().getId(), "ALL", start, Const.LIMIT);
        call.enqueue(new Callback<ReliteRoot>() {
            @Override
            public void onResponse(Call<ReliteRoot> call, Response<ReliteRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getVideo().isEmpty()) {
                        demoReelsAdapter.addData(response.body().getVideo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ReliteRoot> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoAutoPlayHelper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoAutoPlayHelper.play();
    }
}