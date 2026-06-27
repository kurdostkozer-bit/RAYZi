package com.example.rayzi.liveStreamming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityLiveSummaryBinding;
import com.example.rayzi.modelclass.LiveSummaryRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveSummaryActivity extends BaseActivity {
    ActivityLiveSummaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_summary);
        if (!isFinishing()) {
            binding.imgUser.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 40);
            MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                    new BlurTransformation(50),
                    new CenterCrop()
            );
        }

        Intent intent = getIntent();
        String liveStreamingId = intent.getStringExtra(Const.DATA);
        assert liveStreamingId != null;
        if (!liveStreamingId.isEmpty()) {
            getLiveSummaryData(liveStreamingId);
        }

        binding.tvName.setText(sessionManager.getUser().getName());
        binding.btnHomePage.setOnClickListener(v -> onBackPressed());

    }

    private void getLiveSummaryData(String liveStreamingId) {
        customDialogClass.show();
        Call<LiveSummaryRoot> call = RetrofitBuilder.create().getLiveSummary(liveStreamingId);
        call.enqueue(new Callback<LiveSummaryRoot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<LiveSummaryRoot> call, @NonNull Response<LiveSummaryRoot> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().isStatus()) {
                        LiveSummaryRoot.LiveStreamingHistory summary = response.body().getLiveStreamingHistory();
                        binding.tvComments.setText(String.valueOf(summary.getComments()));
                        binding.tvDuration.setText(summary.getDuration());
                        binding.tvIncresedFans.setText("+" + summary.getFans());
                        binding.tvJoinedUsers.setText(String.valueOf(summary.getUser()));
                        binding.tvRcoins.setText("+" + summary.getRCoin());
                        binding.tvRecivedGifts.setText(String.valueOf(summary.getGifts()));
                        customDialogClass.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<LiveSummaryRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}