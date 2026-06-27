package com.example.rayzi.user;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityMyLevelListBinding;
import com.example.rayzi.modelclass.LevelRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLevelListActivity extends BaseActivity {

    ActivityMyLevelListBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_level_list);

        Glide.with(this).load(BuildConfig.BASE_URL + sessionManager.getUser().getLevel().getImage()).into(binding.myLevelImage);
        binding.tvMyLevel.setText(String.valueOf(sessionManager.getUser().getLevel().getName()));
        binding.tvSpentCoin.setText(getString(R.string.you_spent) + sessionManager.getUser().getSpentCoin() + Const.CoinName);
        getLevelData();

    }

    private void getLevelData() {
        customDialogClass.show();
        Call<LevelRoot> call = RetrofitBuilder.create().getLevels();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LevelRoot> call, @NonNull Response<LevelRoot> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().isStatus() && !response.body().getLevel().isEmpty()) {
                        binding.rvFeed.setAdapter(new LevelsAdapter(response.body().getLevel()));
                    }
                }
                customDialogClass.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<LevelRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}