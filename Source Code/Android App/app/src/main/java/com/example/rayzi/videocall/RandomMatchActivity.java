package com.example.rayzi.videocall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.activity.FakeCallRequestActivity;
import com.example.rayzi.databinding.ActivityRandomMatchBinding;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.Gson;

import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomMatchActivity extends BaseActivity {
    ActivityRandomMatchBinding binding;

    private Animation zoomin;
    private Animation animZoomin;
    private GuestProfileRoot.User guestUser;

    private static final String[] REQUESTED_PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_random_match);

        if (!isFinishing()) {
            binding.ivUser.setUserImage(sessionManager.getUser().getImage(), sessionManager.getUser().getAvatarFrameImage(), 30);
            MultiTransformation<Bitmap> transformations = new MultiTransformation<>(
                    new BlurTransformation(50),
                    new CenterCrop()
            );
            Glide.with(this).load(sessionManager.getUser().getImage())
                    .transform(transformations).into(binding.backBlurImage);
        }

        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        animZoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);


        matchAgain();


        binding.btnMatch.setOnClickListener(v -> matchAgain());
        binding.btnCall.setOnClickListener(v -> makeACall());
    }

    private void makeACall() {
        onBackPressed();

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,  getString(R.string.need_permissions) + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA, Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,  getString(R.string.need_permissions) + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA, Toast.LENGTH_SHORT).show();
        } else {
            if (sessionManager.getUser().getDiamond() >= sessionManager.getSetting().getCallCharge()) {
                if (guestUser.isFake()) {
                    startActivity(new Intent(this, FakeCallRequestActivity.class).putExtra(Const.IS_FROM_RANDOM, true).putExtra(Const.USER, new Gson().toJson(guestUser)));
                } else {
                    startActivity(new Intent(this, CallRequestActivity.class).putExtra(Const.USER, new Gson().toJson(guestUser)).putExtra("type", getIntent().getStringExtra("type")).putExtra("random",true));
                }
            } else {
                Toast.makeText(RandomMatchActivity.this, R.string.insufficient_coins , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void matchAgain() {
        binding.lytStatus.setText("Searching for new Friends...");

        binding.ivUser2.setVisibility(View.GONE);
        binding.btnCall.setVisibility(View.GONE);
        binding.btnMatch.setVisibility(View.GONE);
        binding.ivUser.startAnimation(animZoomin);
        binding.ivMatch.setVisibility(View.VISIBLE);

        Call<GuestProfileRoot> call = RetrofitBuilder.create().getRandomUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<GuestProfileRoot>() {
            @Override
            public void onResponse(Call<GuestProfileRoot> call, Response<GuestProfileRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        guestUser = response.body().getUser();
                        setGuestUser();
                    } else {
                        Toast.makeText(RandomMatchActivity.this, R.string.no_one_found_online , Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<GuestProfileRoot> call, Throwable t) {

            }
        });
    }

    private void setGuestUser() {

        if (!isFinishing()) {
            binding.ivUser2.setUserImage(guestUser.getImage(), guestUser.getAvatarFrameImage(), 30);
        }
        binding.lytStatus.setText(getString(R.string.matched_with) + guestUser.getName());

        binding.ivUser.clearAnimation();
        binding.ivUser2.setVisibility(View.VISIBLE);
        binding.btnMatch.setVisibility(View.VISIBLE);
        binding.btnCall.setVisibility(View.VISIBLE);
        binding.ivMatch.setVisibility(View.GONE);


    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}