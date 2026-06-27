package com.example.rayzi.ads;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.dilog.CustomDialogClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MyRewardAds {


    private static final String TAG = "MyRewardAds ";
    private RewardedAd mRewardedAd;
    private Context context;
    private RewardAdListnear rewardAdListnear;
    SessionManager sessionManager;
    // Called when ad is shown.
    // Called when ad fails to show.
    // Called when ad is dismissed.
    // Set the ad reference to null so you don't show the ad a second time.
    FullScreenContentCallback callBack = new FullScreenContentCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "Ad was shown.");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            // Called when ad fails to show.
            Log.d(TAG, "Ad failed to show.");
            rewardAdListnear.onEarned();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Log.d(TAG, "Ad was dismissed.");
            mRewardedAd = null;
        }
    };

    public MyRewardAds(Context context, RewardAdListnear rewardAdListnear) {
        this.context = context;
        sessionManager = new SessionManager(context);
        this.rewardAdListnear = rewardAdListnear;

        if (sessionManager.getAds() != null && sessionManager.getAds().isShow() && sessionManager.getAds().getReward() != null) {
            initGoogle();
        }

    }

    public void initGoogle() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(callBack);
                        Log.d(TAG, "Ad was loaded.");
                    }
                });


    }


    public void showAds(Activity activity, CustomDialogClass customDialogClass, ImageView clickableView) {
        if (mRewardedAd != null) {
            clickableView.setClickable(false);
            clickableView.setAlpha(0.5f);

            Activity activityContext = activity;
            mRewardedAd.show(activityContext, rewardItem -> {
                Log.d(TAG, "The user earned the reward.");
                customDialogClass.dismiss();
                rewardAdListnear.onEarned();

                clickableView.setClickable(true);
                clickableView.setAlpha(1f);
                clickableView.setImageDrawable(activity.getResources().getDrawable(R.drawable.watch_ad_btn));
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    public interface RewardAdListnear {
        void onAdClosed();

        void onEarned();
    }

}
