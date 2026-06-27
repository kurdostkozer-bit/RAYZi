package com.example.rayzi.user.freeCoins;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.ads.MyRewardAds;
import com.example.rayzi.databinding.ActivityFreeDimondsBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeDimondsActivity extends BaseActivity implements MyRewardAds.RewardAdListnear {
    ActivityFreeDimondsBinding binding;
    MyRewardAds myRewardAds;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_free_dimonds);
        getWindow().setStatusBarColor(Color.parseColor("#170D1F"));
        myRewardAds = new MyRewardAds(this, this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
        binding.tvCode.setText(sessionManager.getUser().getReferralCode());
        binding.lytreferCount.setText("You have " + sessionManager.getUser().getReferralCount() + " referrals");
        binding.tvReferString.setText("Invite your Friends! for each payout of invited friends, you and your friend both will receive " + "" + sessionManager.getSetting().getReferralBonus() + "" + " Diamonds.");
        binding.lytAds.setOnClickListener(v -> {
            watchVideoClick();
        });
        binding.etRefercode.requestFocus();
        if (isRTL(this)) {
            binding.backimg.setScaleX(isRTL(this) ? -1 : 1);
        }
    }

    private void watchVideoClick() {
        Log.d(TAG, "onCreate: clicked");
        customDialogClass.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.lytAds.setImageDrawable(getResources().getDrawable(R.drawable.watch_ad_btn_disable));

                if (sessionManager.getUser().getAd().getCount() < sessionManager.getSetting().getMaxAdPerDay()) {
                    binding.lytAds.setClickable(true);
                    myRewardAds.showAds(FreeDimondsActivity.this, customDialogClass, binding.lytAds);
                } else {
                    Toast.makeText(FreeDimondsActivity.this, "You exceed your Ad limit.", Toast.LENGTH_SHORT).show();
                    binding.lytAds.setAlpha(0.5f);
                    customDialogClass.dismiss();
                    binding.lytAds.setClickable(false);
                }
            }
        }, 1000);

    }

    private void initView() {
        Shader shader = new LinearGradient(0, 0, 0, binding.tvMethod1.getLineHeight(),
                getResources().getColor(R.color.gradient_1), getResources().getColor(R.color.gradient_2), Shader.TileMode.REPEAT);
        binding.tvMethod1.getPaint().setShader(shader);
        binding.tvMethod2.getPaint().setShader(shader);
    }

    public void onClickCopy(View view) {

        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", binding.tvCode.getText().toString());
        if (manager != null) {
            manager.setPrimaryClip(clipData);
            Toast.makeText(this, R.string.referral_code_copied, Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickShare(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareMessage = shareMessage + "Here is My Referral Code " + sessionManager.getUser().getReferralCode().toUpperCase(Locale.ROOT);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_one)));
        } catch (Exception e) {
            //ll
        }
    }

    public void onClickSubmit(View view) {
        String referCode = binding.etRefercode.getText().toString();
        if (referCode.isEmpty()) {
            Toast.makeText(this, R.string.enter_refer_code, Toast.LENGTH_SHORT).show();
            return;
        }
        binding.submitBtn.setEnabled(false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("referralCode", referCode);
        Call<UserRoot> call = RetrofitBuilder.create().reedemReferalCode(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(FreeDimondsActivity.this, R.string.refereed_successfully, Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                    } else {
                        if (response.body().getMessage() != null) {
                            Toast.makeText(FreeDimondsActivity.this, response.body().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                binding.submitBtn.setEnabled(true);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                binding.submitBtn.setEnabled(true);
            }
        });

    }

    @Override
    public void onAdClosed() {
        myRewardAds = new MyRewardAds(this, this);
    }

    @Override
    public void onEarned() {
        myRewardAds = new MyRewardAds(this, this);
        submitData();
    }

    private void submitData() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        Call<UserRoot> call = RetrofitBuilder.create().addDiamondFromAds(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus()) {

                    sessionManager.saveUser(response.body().getUser());
                    Toast.makeText(FreeDimondsActivity.this, R.string.earned_by_user, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FreeDimondsActivity.this, getString(R.string.something_went_wrong_text), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }
}