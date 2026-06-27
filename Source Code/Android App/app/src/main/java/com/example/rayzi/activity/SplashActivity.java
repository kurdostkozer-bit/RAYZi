package com.example.rayzi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.audioLive.LiveStreamRoot;
import com.example.rayzi.modelclass.BannerRoot;
import com.example.rayzi.modelclass.GiftCategoryRoot;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.IpAddressRoot_e;
import com.example.rayzi.modelclass.SettingRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.modelclass.VipPlanRoot;
import com.example.rayzi.pkRoot.PkAudioLiveUserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.retrofit.UserApiCall;
import com.example.rayzi.utils.SvgaCacheManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.branch.referral.Branch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "spleshact";
    SessionManager sessionManager;
    private String branchData = "";
    private String type = "";
    private int totalCategories;
    private int currentIndex;

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession((referringParams, error) -> {
            if (error == null) {
                Log.i("BRANCH SDK1", referringParams.toString());
                try {
                    boolean isLinkClicked = referringParams.getBoolean("+clicked_branch_link");
                    Log.d(TAG, "onStart:is link clicked   " + isLinkClicked);

                    if (isLinkClicked) {
                        branchData = referringParams.getString(Const.DATA);
                        type = referringParams.getString(Const.TYPE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.i("BRANCH SDK2", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);


        ((TextView)findViewById(R.id.tvVersion)).setText("version " + BuildConfig.VERSION_CODE);

        GlideBuilder builder = new GlideBuilder();
        builder.setMemoryCache(new LruResourceCache(20 * 1024 * 1024)) // 20 MB memory cache
                .setDiskCache(new InternalCacheDiskCacheFactory(this, 100 * 1024 * 1024)); // 100 MB disk cache
        Glide.init(this, builder);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager = new SessionManager(this);

        MobileAds.initialize(this, initializationStatus -> {
        });
        String s = Build.SERIAL;
        Log.d(TAG, "onCreate: serial " + s);
        //  getIp();
        FirebaseMessaging.getInstance().subscribeToTopic("CHAPI").addOnCompleteListener(task -> Log.d("TAG", "onCreate: init msg"));

        checkNetwork();
        getBanner();
        getGiftCategory();
        getFakeLiveList();
        getVipPlan();
    }


    public void getBanner() {

        Call<BannerRoot> call = RetrofitBuilder.create().getBanner("hello");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<BannerRoot> call, Response<BannerRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getBanner().isEmpty()) {
                        sessionManager.saveBannerList(response.body().getBanner());
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getFakeLiveList() {
        Call<PkAudioLiveUserRoot> call = RetrofitBuilder.create().getFakeLiveList(0, Const.LIMIT30);
        call.enqueue(new Callback<PkAudioLiveUserRoot>() {
            @Override
            public void onResponse(@NonNull Call<PkAudioLiveUserRoot> call, @NonNull Response<PkAudioLiveUserRoot> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().isStatus()) {
                        if (response.body().getUsers() != null && !response.body().getUsers().isEmpty()) {
                            sessionManager.saveFakeLiveList(response.body().getUsers());
                        }else{
                            sessionManager.saveFakeLiveList(new ArrayList<>());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PkAudioLiveUserRoot> call, Throwable t) {

            }
        });
    }

    public void getGiftCategory() {
        Call<GiftCategoryRoot> call = RetrofitBuilder.create().getGiftCategory();
        call.enqueue(new Callback<GiftCategoryRoot>() {
            @Override
            public void onResponse(Call<GiftCategoryRoot> call, Response<GiftCategoryRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getCategory().isEmpty()) {
                        sessionManager.saveGiftCategories(response.body().getCategory());
                        totalCategories = response.body().getCategory().size();
                        currentIndex = 0;
                        fetchNextGiftList(currentIndex, totalCategories, response.body().getCategory());

                        Log.d(TAG, "onResponse: sessionManager.getGiftCategoriesList() ====== " + sessionManager.getGiftCategoriesList().toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<GiftCategoryRoot> call, Throwable t) {

            }
        });
    }

    public void getVipPlan(){
        Call<VipPlanRoot> call = RetrofitBuilder.create().getVipPlan();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<VipPlanRoot> call, Response<VipPlanRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getVipPlan().isEmpty()) {
                        sessionManager.saveVipPlan(response.body().getVipPlan());
                    }
                }
            }

            @Override
            public void onFailure(Call<VipPlanRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void fetchNextGiftList(final int currentIndex, final int totalCategories, List<GiftCategoryRoot.CategoryItem> category) {
        if (currentIndex >= totalCategories) {
            // All requests are done
            return;
        }
        Log.d(TAG, "fetchNextGiftList: currentIndex ==== " + currentIndex);
        String categoryId = category.get(currentIndex).getId();
        getGiftsList(categoryId, currentIndex, new GiftListCallback() {
            @Override
            public void onGiftListFetched(int currentIndex) {
                // Gift list for the current category fetched
                // You can perform any processing with the response here

                // Continue to the next category
                fetchNextGiftList(currentIndex + 1, totalCategories, category);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
            }
        });
    }

    private void getGiftsList(String id, int currentIndex, final GiftListCallback callback) {
        Call<GiftRoot> call = RetrofitBuilder.create().getGiftsByCategory(id);
        call.enqueue(new Callback<GiftRoot>() {
            @Override
            public void onResponse(Call<GiftRoot> call, Response<GiftRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getGift().isEmpty()) {
                        // You can perform any processing with the response here
                        sessionManager.saveGiftsList(response.body().getGift().get(0).getCategory(), response.body().getGift());
                        Log.d(TAG, "onResponse: getSVGAGiftsList call thay ceh =======");
                        Log.d(TAG, "onResponse: getSVGAGiftsList response.body().getGift().get(0).getCategory() " + response.body().getGift().get(0).getCategory());
                        Log.d(TAG, "onResponse: getSVGAGiftsList call thay ceh id ===    =======" + id);
                        // Notify the callback that the current category's gift list has been fetched

                        List<GiftRoot.GiftItem> gifts = response.body().getGift();
                        for (GiftRoot.GiftItem gift : gifts) {


                            if (gift.getType() == 2) {
                                // Build the full URL
                                String svgaUrl = BuildConfig.BASE_URL + gift.getImage();
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.execute(() -> {
                                    // This runs in a background thread
                                    SvgaCacheManager.downloadAndCacheSvga(svgaUrl, SplashActivity.this);
                                    // Then post back to main thread if you need to update UI
                                });
                                // 1) Download & cache the .svga file
                              /*  File cachedFile = SvgaCacheManager.downloadAndCacheSvga(svgaUrl, SplashActivity.this);
                                if (cachedFile != null) {
                                    // 2) Optionally decode the cached file immediately
                                    SvgaCacheManager.decodeSvgaFromCache(
                                            SplashActivity.this,    // context
                                            svgaUrl,            // original URL
                                            new SVGAParser.ParseCompletion() {
                                                @Override
                                                public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                                                    // onSuccess: do something, e.g., preload / store in memory, etc.
                                                    Log.d("SVGA", "Successfully decoded from cache: " + svgaUrl);
                                                }

                                                @Override
                                                public void onError() {
                                                    Log.e("SVGA", "Failed to decode from cache: " + svgaUrl);
                                                }
                                            }
                                    );
                                } else {
                                    // Download failed or something else happened
                                    Log.e("SVGA", "Error caching file for URL: " + svgaUrl);
                                }*/
                            }
                        }

                        Log.d(TAG, "onResponse: getGiftsList =================== " + sessionManager.getGiftsList(response.body().getGift().get(0).getCategory()));
                        callback.onGiftListFetched(currentIndex);
                    }
                }
            }

            @Override
            public void onFailure(Call<GiftRoot> call, Throwable t) {
                // Handle the error
                callback.onError(t.getMessage());
            }
        });
    }

    public interface GiftListCallback {
        void onGiftListFetched(int currentIndex);

        void onError(String errorMessage);
    }

    private void getIp() {
        Call<IpAddressRoot_e> call = RetrofitBuilder.getIp().getIp();
        call.enqueue(new Callback<IpAddressRoot_e>() {
            @Override
            public void onResponse(Call<IpAddressRoot_e> call, Response<IpAddressRoot_e> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getCountry() != null) {
                        Log.d("TAG", "onResponse: get ip");

                        sessionManager.saveStringValue(Const.COUNTRY, response.body().getCountry());
                        sessionManager.saveStringValue(Const.CURRENT_CITY, response.body().getCity());
                        if (response.body().getQuery() != null) {
                            sessionManager.saveStringValue(Const.IPADDRESS, response.body().getQuery());
                        }
                        getSetting();
                    } else {

                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<IpAddressRoot_e> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());

            }
        });
    }

    private void getSetting() {
        Log.d(TAG, "getSetting: getSettings =======================================");
        Call<SettingRoot> call = RetrofitBuilder.create().getSettings();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SettingRoot> call, Response<SettingRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveSetting(response.body().getSetting());
                        ((MainApplication) getApplication()).initAgora(SplashActivity.this);
                        Const.setCurrency(sessionManager.getSetting().getCurrency());

                        if (updateApp()) return;

                        if (sessionManager.getSetting().isIsAppActive()) {
                            gotoMainPage();
                        } else {
                            new PopupBuilder(SplashActivity.this).showSimplePopup(getString(R.string.we_are_under_maintenance_text), getString(R.string.dismiss), () -> finishAffinity());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SettingRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: getSettings =================================================  " + t.getMessage());
            }
        });


    }

    private boolean updateApp() {
        if (sessionManager.getSetting().getLetestVersonCode() > BuildConfig.VERSION_CODE) {
            new PopupBuilder(SplashActivity.this).updatePopup(SplashActivity.this, getString(R.string.continue_text), getString(R.string.go_back), getString(R.string.new_update_available), new PopupBuilder.OnMultButtonPopupLister() {
                @Override
                public void onClickCountinue() {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    finishAffinity();
                }

                @Override
                public void onClickCancel() {
                    finishAffinity();
                }
            });
            return true;
        }
        return false;
    }

    private void gotoMainPage() {
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
                UserApiCall userApiCall = new UserApiCall(this);
                userApiCall.getUser(new UserApiCall.OnUserApiListner() {
                    @Override
                    public void onUserGetted(UserRoot.User user) {
                        if (user.isIsBlock()) {
                            new PopupBuilder(SplashActivity.this).showBlockPopup(getString(R.string.you_are_blocked_by_admin_text), getString(R.string.dismiss), () -> finishAffinity());
                        } else {
                            checkUser(user);
                        }
                    }

                    @Override
                    public void onUserStatusFailed(String message) {
                        if (message.contains("User does not Exist")) {
                            Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, LoginActivityActivity.class));
                            finish();
                        } else {
                            new PopupBuilder(SplashActivity.this).showBlockPopup(getString(R.string.you_are_blocked_by_admin_text), getString(R.string.cancel), () -> finishAffinity());
                        }
                    }
                });


            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivityActivity.class));
            }
        }, 500);
    }

    private void checkUser(UserRoot.User user) {
        Log.d(TAG, "checkUser: local Id " + sessionManager.getUser().getIdentity());
        Log.d(TAG, "checkUser: remote Id " + user.getIdentity());
        if (user.getIdentity().equals(sessionManager.getUser().getIdentity())) {
            sessionManager.saveUser(user);
            checkHostLiveOrNot();
            startActivity(new Intent(SplashActivity.this, MainActivity.class)
                    .putExtra(Const.DATA, branchData).putExtra(Const.TYPE, type));
        } else {
            new PopupBuilder(this).showSimplePopup(getString(R.string.you_are_logged_in_other_devices), getString(R.string.dismiss), () -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();

                Toast.makeText(this, getString(R.string.log_out), Toast.LENGTH_SHORT).show();

                sessionManager.saveUser(null);
                sessionManager.saveBooleanValue(Const.ISLOGIN, false);
                startActivity(new Intent(SplashActivity.this, LoginActivityActivity.class));
                finish();

            });
        }

    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        boolean isConnected2 = activeNetInfo2 != null && activeNetInfo2.isConnectedOrConnecting();
        showHideInternet(isConnected || isConnected2);
    }

    private void showHideInternet(Boolean isOnline) {
        Log.d(TAG, "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            getIp();
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tvInternetStatus.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    tvInternetStatus.startAnimation(animation);
                }, 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tvInternetStatus.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    tvInternetStatus.startAnimation(animation);
                }
            }
        }
    }

    private void checkHostLiveOrNot() {
        if (sessionManager.getUser() != null) {
            Call<LiveStreamRoot> call = RetrofitBuilder.create().checkUserLiveOrNot(sessionManager.getUser().getId());
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<LiveStreamRoot> call, Response<LiveStreamRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            sessionManager.setIsAudioRoomBackground(true);
                            String data = new Gson().toJson(response.body().getLiveUser());
                            sessionManager.saveLiveUserForBackground(new Gson().fromJson(data, PkAudioLiveUserRoot.UsersItem.class));
                        } else {
                            sessionManager.setIsAudioRoomBackground(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LiveStreamRoot> call, Throwable t) {
                    t.printStackTrace();
                }

            });
        }
    }

}