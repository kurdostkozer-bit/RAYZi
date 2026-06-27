
package com.example.rayzi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rayzi.MainApplication;
import com.example.rayzi.NetWorkChangeReceiver;
import com.example.rayzi.R;
import com.example.rayzi.adapter.ScreenSlidePagerAdapter;
import com.example.rayzi.ads.MyRewardAds;
import com.example.rayzi.audioLive.LiveStreamRoot;
import com.example.rayzi.audioLive.WatchAudioLiveActivity;
import com.example.rayzi.databinding.ActivityMainBinding;
import com.example.rayzi.modelclass.PostRoot;
import com.example.rayzi.modelclass.ReliteRoot;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.pk.HostPKLiveActivity;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.popups.PrivacyPopup_g;
import com.example.rayzi.posts.FeedListActivity;
import com.example.rayzi.reels.ReelsActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.socket.MySocketManager;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements MyRewardAds.RewardAdListnear {
    public static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    boolean result = false;
    private NetWorkChangeReceiver netWorkChangeReceiver;

    public static int position;
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private BroadcastReceiver uploadProgressReceiver;
    private boolean isUploadingLytShown = false;
    MyRewardAds myRewardAds;

    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.isAppOpen = true;
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.e(TAG, "onCreate: globalConnecting" + MySocketManager.getInstance().globalConnecting);
        Log.e(TAG, "onCreate: globalConnected" + MySocketManager.getInstance().globalConnected);
        if (!MySocketManager.getInstance().globalConnecting || !MySocketManager.getInstance().globalConnected) {
            getApp().initGlobalSocket();
        }
        checkPermission();
        initBottomBar();
        getStrickers();
        getAdsKeys();

        myRewardAds = new MyRewardAds(this, this);


    }

    private void checkUserPlan() {
        Call<RestResponse> call = RetrofitBuilder.create().checkUserPlan(sessionManager.getUser().getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {

            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private void initMain() {
        startReceiver();
        handleProgressReceiver();
        handleBranchData();
        makeOnlineUser();
        initView();
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(MainActivity.this);
        binding.viewpagerMain.setAdapter(screenSlidePagerAdapter);
        binding.viewpagerMain.setUserInputEnabled(false);
        binding.ivLive.setOnClickListener(v1 -> {
            startActivity(new Intent(this, GotoLiveActivityNew.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            v1.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
        });

    }

    private void handleProgressReceiver() {
        uploadProgressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Const.UPLOAD_PROGRESS)) {
                    if (!isUploadingLytShown) {
                        isUploadingLytShown = true;
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
                        binding.uploadingImageLyt.setAnimation(animation);
                        binding.uploadingImageLyt.setVisibility(View.VISIBLE);
                    }
                    int progress = intent.getIntExtra("progress", 0);
                    Log.d(TAG, "onMessageEvent: progress== " + progress);
                    binding.progressPercentage.setText(progress + "%");
                    binding.uploadingProgress.setProgress(progress);
                    if (progress >= 96) {
                        isUploadingLytShown = false;
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up);
                        binding.uploadingImageLyt.setAnimation(animation);
                        binding.uploadingImageLyt.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private void initView() {
        checkUserPlan();
    }


    private boolean showPrivacyPopup() {
        if (!sessionManager.getBooleanValue(Const.POLICY_ACCEPTED)) {
            new PrivacyPopup_g(this, new PrivacyPopup_g.OnSubmitClickListnear() {
                @Override
                public void onAccept() {
                    result = true;
                    sessionManager.saveBooleanValue(Const.POLICY_ACCEPTED, true);
                    checkPermission();
                }

                @Override
                public void onDeny() {
                    result = false;
                    finishAffinity();
                }
            });
        } else result = true;
        return result;
    }

    public void checkPermission() {
        List<String> strings = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            strings = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS);
        } else {
            strings = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
        }
        requestPermissionIfNeeded(strings, (allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                if (showPrivacyPopup()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        initMain();
                    }
                }
            } else finishAffinity();
        });
    }

    private void handleBranchData() {
        Intent intent = getIntent();
        String branchData = intent.getStringExtra(Const.DATA);
        String type = intent.getStringExtra(Const.TYPE);
        if (branchData != null && !branchData.isEmpty()) {
            if (type.equals("POST")) {
                PostRoot.PostItem post = new Gson().fromJson(branchData, PostRoot.PostItem.class);
                List<PostRoot.PostItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, FeedListActivity.class)

                        .putExtra(Const.POSITION, 0)
                        .putExtra(Const.DATA, new Gson().toJson(list)));
            } else if (type.equals("RELITE")) {
                ReliteRoot.VideoItem post = new Gson().fromJson(branchData, ReliteRoot.VideoItem.class);
                List<ReliteRoot.VideoItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, ReelsActivity.class).putExtra(Const.POSITION, 0).putExtra(Const.DATA, new Gson().toJson(list)));
            } else if (type.equals("PROFILE")) {
                String userId = branchData;
                startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, userId));

            } else if (type.equals("LIVE")) {
                LiveStreamRoot.LiveUser usersItem = new Gson().fromJson(branchData, LiveStreamRoot.LiveUser.class);      // this is for pk
                Log.d("TAG", "handleBranchData: live  " + usersItem.toString());
                startActivity(new Intent(this, HostPKLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(usersItem)));
            } else if (type.equals("AUDIO_LIVE")) {
                PkAudioLiveUserRoot.UsersItem usersItem = new Gson().fromJson(branchData, PkAudioLiveUserRoot.UsersItem.class);
                startActivity(new Intent(this, WatchAudioLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(usersItem)));
            }
        }
    }

    private void setUpFragment(int position) {
        binding.viewpagerMain.setCurrentItem(position);
    }

    @SuppressLint("NonConstantResourceId")
    private void initBottomBar() {
        binding.bottomNavigationView.setItemIconTintList(null);
        binding.bottomNavigationView.setSelectedItemId(R.id.miHome);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.miHome -> {
                    position = 0;
                    setUpFragment(0);
                    return true;
                }
                case R.id.miRandomCall -> {
                    position = 1;
                    setUpFragment(1);
                    return true;
                }
                case R.id.miFeed -> {
                    position = 2;
                    setUpFragment(2);
                    return true;
                }
                case R.id.miMessage -> {
                    position = 3;
                    setUpFragment(3);
                    return true;
                }
                default -> {
                    return false;
                }
            }
        });

    }


    protected void startReceiver() {
        netWorkChangeReceiver = new NetWorkChangeReceiver(this::showHideInternet);
        registerNetworkBroadcastForNougat();
    }

    private void registerNetworkBroadcastForNougat() {
        registerReceiver(netWorkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(netWorkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private void showHideInternet(Boolean isOnline) {
        Log.d("TAG", "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> slideToTop(tvInternetStatus), 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    slideToBottom(tvInternetStatus);
                }
            }
        }
    }

    private void slideToTop(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }

    private void slideToBottom(final View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }

    public void changeFragment(int position, int id) {
        this.position = position;
        binding.bottomNavigationView.setSelectedItemId(id);
        setUpFragment(position);
    }

    @Override
    public void onBackPressed() {
        // Check if the user is on the home fragment (position 0)
        if (binding.viewpagerMain.getCurrentItem() == 0) {
            // Show exit dialog
            new PopupBuilder(this).showExitPopup(super::onBackPressed);
        } else {
            // Navigate back to the home fragment
            binding.viewpagerMain.setCurrentItem(0, true);
            binding.bottomNavigationView.setSelectedItemId(R.id.miHome);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isPKRunning) binding.pkLayout.setVisibility(View.VISIBLE);
        else binding.pkLayout.setVisibility(View.GONE);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(uploadProgressReceiver, new IntentFilter("UPLOAD_PROGRESS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadProgressReceiver);
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        MainApplication.isAppOpen = false;
        super.onDestroy();
    }

    @Override
    public void onAdClosed() {

    }

    @Override
    public void onEarned() {

    }
}